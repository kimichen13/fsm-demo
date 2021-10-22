package com.qqviaja.fsm.config;

import com.qqviaja.fsm.dao.Request;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
public class InMemoryPersist implements StateMachinePersist<String, String, Request> {

    @Override
    public void write(StateMachineContext<String, String> stateMachineContext, Request request) throws Exception {

    }

    @Override
    public StateMachineContext<String, String> read(Request request) throws Exception {
        return new DefaultStateMachineContext<>(request.getState(), null, null,null,null, request.getMachineId());
    }
}
