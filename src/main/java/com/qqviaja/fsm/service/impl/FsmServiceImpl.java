package com.qqviaja.fsm.service.impl;

import com.qqviaja.fsm.common.FsmConstant;
import com.qqviaja.fsm.config.StatefulStateMachineService;
import com.qqviaja.fsm.dao.Request;
import com.qqviaja.fsm.dao.RequestRepository;
import com.qqviaja.fsm.service.IFsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
@Service
public class FsmServiceImpl implements IFsmService {

    @Autowired
    private StatefulStateMachineService<String, String, Integer, Request> service;
    @Autowired
    private RequestRepository requestRepository;

    @Override
    public Request createRequest() {
        final String initialState = service.acquireStateMachineInitialState(FsmConstant.REQUEST_MACHINE_ID);
        final Request entity = new Request();
        entity.setState(initialState);
        final Request save = requestRepository.save(entity);
        service.acquireStateMachine(save, false);
        return save;
    }

    @Override
    public Request execute(Integer requestId, String event) {
        return requestRepository.findById(requestId)
                .map(request -> {
                    final StateMachine<String, String> stateMachine = service.acquireStateMachine(request);
                    stateMachine.sendEvent(MessageBuilder.withPayload(event).setHeader("request", request).build());
                    request.setState(stateMachine.getState().getId());
                    service.releaseStateMachine(request);
                    return requestRepository.save(request);
                }).orElseThrow(()-> new RuntimeException("No Request Found by Id" + requestId));
    }
}
