package com.qqviaja.fsm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.Lifecycle;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
@Slf4j
public class DefaultStatefulStateMachineService<S, E, ID, T extends Stateful<ID>> implements StatefulStateMachineService<S, E, ID, T>, DisposableBean {

    private final StateMachineFactory<S, E> stateMachineFactory;
    private final Map<ID, StateMachine<S, E>> machines = new HashMap<>();
    private StateMachinePersist<S, E, T> stateMachinePersist;

    /**
     * Instantiates a new default state machine service.
     *
     * @param stateMachineFactory the state machine factory
     */
    public DefaultStatefulStateMachineService(StateMachineFactory<S, E> stateMachineFactory) {
        this(stateMachineFactory, null);
    }

    /**
     * Instantiates a new default state machine service.
     *
     * @param stateMachineFactory the state machine factory
     * @param stateMachinePersist the state machine persist
     */
    public DefaultStatefulStateMachineService(StateMachineFactory<S, E> stateMachineFactory,
                                              StateMachinePersist<S, E, T> stateMachinePersist) {
        Assert.notNull(stateMachineFactory, "'stateMachineFactory' must be set");
        this.stateMachineFactory = stateMachineFactory;
        this.stateMachinePersist = stateMachinePersist;
    }

    @Override
    public final void destroy() throws Exception {
        doStop();
    }

    @Override
    public S acquireStateMachineInitialState(String machineId) {
        return stateMachineFactory.getStateMachine(machineId).getInitialState().getId();
    }

    @Override
    public StateMachine<S, E> acquireStateMachine(T t) {
        return acquireStateMachine(t, true);
    }

    @Override
    public StateMachine<S, E> acquireStateMachine(T t, boolean start) {
        final String machineId = t.getMachineId();
        final ID id = t.getId();
        log.info(String.format("Acquiring machine with id: %s, state: %s for Stateful Id: %s", machineId, t.getState(), id));
        StateMachine<S, E> stateMachine;
        // naive sync to handle concurrency with release
        synchronized (machines) {
            stateMachine = machines.get(id);
            if (stateMachine == null) {
                log.info("Getting new machine from factory with id " + machineId);
                stateMachine = stateMachineFactory.getStateMachine(machineId);
                if (stateMachinePersist != null) {
                    try {
                        final StateMachineContext<S, E> stateMachineContext = stateMachinePersist.read(t);
                        stateMachine = restoreStateMachine(stateMachine, stateMachineContext);
                    } catch (Exception e) {
                        log.error("Error handling context", e);
                        throw new StateMachineException("Unable to read context from store", e);
                    }
                }
                machines.put(id, stateMachine);
            }
        }
        // handle start outside of sync as it might take some time and would block other machines acquire
        return handleStart(stateMachine, start);
    }

    @Override
    public void releaseStateMachine(T t) {
        final String machineId = t.getMachineId();
        final ID id = t.getId();
        log.info(String.format("Releasing machine with id: %s, Stateful Id: %s", machineId, id));
        synchronized (machines) {
            StateMachine<S, E> stateMachine = machines.remove(id);
            if (stateMachine != null) {
                log.info(String.format("Found machine with id: %s, Stateful Id: %s", machineId, id));
                stateMachine.stop();
            }
        }
    }

    @Override
    public void releaseStateMachine(T t, boolean stop) {
        final String machineId = t.getMachineId();
        final ID id = t.getId();
        log.info(String.format("Releasing machine with id: %s, Stateful Id: %s", machineId, id));
        synchronized (machines) {
            StateMachine<S, E> stateMachine = machines.remove(id);
            if (stateMachine != null) {
                log.info(String.format("Found machine with id: %s, Stateful Id: %s", machineId, id));
                handleStop(stateMachine, stop);
            }
        }
    }


    /**
     * Determines if the given machine identifier denotes a known managed state machine.
     *
     * @param id Stateful identifier
     * @return true if Stateful ID denotes a known managed state machine currently in memory
     */
    public boolean hasStateMachine(ID id) {
        synchronized (machines) {
            return machines.containsKey(id);
        }
    }

    /**
     * Sets the state machine persist.
     *
     * @param stateMachinePersist the state machine persist
     */
    public void setStateMachinePersist(StateMachinePersist<S, E, T> stateMachinePersist) {
        this.stateMachinePersist = stateMachinePersist;
    }

    protected void doStop() {
        log.info("Entering stop sequence, stopping all managed machines");
        synchronized (machines) {
            machines.values().forEach(stateMachine -> handleStop(stateMachine, true));
            machines.clear();
        }
    }

    protected StateMachine<S, E> restoreStateMachine(StateMachine<S, E> stateMachine, final StateMachineContext<S, E> stateMachineContext) {
        if (stateMachineContext == null) {
            return stateMachine;
        }
        stateMachine.stop();
        // only go via top region
        stateMachine.getStateMachineAccessor().doWithRegion(function -> function.resetStateMachine(stateMachineContext));
        return stateMachine;
    }

    protected StateMachine<S, E> handleStart(StateMachine<S, E> stateMachine, boolean start) {
        if (start) {
            if (!((Lifecycle) stateMachine).isRunning()) {
                StartListener<S, E> listener = new StartListener<>(stateMachine);
                stateMachine.addStateListener(listener);
                stateMachine.start();
                try {
                    listener.latch.await();
                } catch (InterruptedException e) {
                }
            }
        }
        return stateMachine;
    }

    protected StateMachine<S, E> handleStop(StateMachine<S, E> stateMachine, boolean stop) {
        if (stop) {
            if (((Lifecycle) stateMachine).isRunning()) {
                StopListener<S, E> listener = new StopListener<>(stateMachine);
                stateMachine.addStateListener(listener);
                stateMachine.stop();
                try {
                    listener.latch.await();
                } catch (InterruptedException e) {
                }
            }
        }
        return stateMachine;
    }

    private static class StartListener<S, E> extends StateMachineListenerAdapter<S, E> {

        final CountDownLatch latch = new CountDownLatch(1);
        final StateMachine<S, E> stateMachine;

        public StartListener(StateMachine<S, E> stateMachine) {
            this.stateMachine = stateMachine;
        }

        @Override
        public void stateMachineStarted(StateMachine<S, E> stateMachine) {
            this.stateMachine.removeStateListener(this);
            latch.countDown();
        }
    }

    private static class StopListener<S, E> extends StateMachineListenerAdapter<S, E> {

        final CountDownLatch latch = new CountDownLatch(1);
        final StateMachine<S, E> stateMachine;

        public StopListener(StateMachine<S, E> stateMachine) {
            this.stateMachine = stateMachine;
        }

        @Override
        public void stateMachineStopped(StateMachine<S, E> stateMachine) {
            this.stateMachine.removeStateListener(this);
            latch.countDown();
        }
    }
}
