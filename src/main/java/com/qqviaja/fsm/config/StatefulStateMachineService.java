package com.qqviaja.fsm.config;

import org.springframework.statemachine.StateMachine;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
public interface StatefulStateMachineService<S, E, ID, T extends Stateful<ID>> {

    S acquireStateMachineInitialState(String machineId);

    StateMachine<S, E> acquireStateMachine(T t);

    StateMachine<S, E> acquireStateMachine(T t, boolean start);

    void releaseStateMachine(T t);

    void releaseStateMachine(T t, boolean stop);
}
