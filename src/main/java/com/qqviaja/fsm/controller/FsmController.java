package com.qqviaja.fsm.controller;

import com.qqviaja.fsm.common.FsmConstant;
import com.qqviaja.fsm.dao.Request;
import com.qqviaja.fsm.service.IFsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.data.jpa.JpaRepositoryState;
import org.springframework.statemachine.data.jpa.JpaRepositoryTransition;
import org.springframework.statemachine.data.jpa.JpaStateRepository;
import org.springframework.statemachine.data.jpa.JpaTransitionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Create on 2021/10/2.</p>
 *
 * @author Kimi Chen
 */
@RestController
public class FsmController {

    @Autowired
    private IFsmService fsmService;

    @Autowired
    private JpaStateRepository stateRepository;
    @Autowired
    private JpaTransitionRepository transitionRepository;

    @GetMapping("/createRequest")
    public Request createRequest() {
        return fsmService.createRequest();
    }

    @GetMapping("/sendEvent")
    public Request sendEvent(Integer requestId, String event) {
        return fsmService.execute(requestId, event);
    }

    @GetMapping("/initial")
    public void initial() {
        final JpaRepositoryState inProcess = new JpaRepositoryState(FsmConstant.REQUEST_MACHINE_ID, "IN_PROCESS", true);
        final JpaRepositoryState pendingApproval = new JpaRepositoryState(FsmConstant.REQUEST_MACHINE_ID, "PENDING_APPROVAL", false);
        final JpaRepositoryState archive = new JpaRepositoryState(FsmConstant.REQUEST_MACHINE_ID, "ARCHIVE", false);
        final JpaRepositoryState historical = new JpaRepositoryState(FsmConstant.REQUEST_MACHINE_ID, "HISTORICAL", false);
        final JpaRepositoryState obsolete = new JpaRepositoryState(FsmConstant.REQUEST_MACHINE_ID, "OBSOLETE", false);

        stateRepository.saveAll(Arrays.asList(inProcess, pendingApproval, archive, historical, obsolete));

        final JpaRepositoryTransition submit = new JpaRepositoryTransition(inProcess, pendingApproval, "SUBMIT");
        final JpaRepositoryTransition reject = new JpaRepositoryTransition(pendingApproval, inProcess, "REJECT");
        final JpaRepositoryTransition approve = new JpaRepositoryTransition(pendingApproval, archive, "APPROVE");
        final JpaRepositoryTransition originalCopy = new JpaRepositoryTransition(archive, historical, "ORIGINAL_COPY");
        final JpaRepositoryTransition newCopy = new JpaRepositoryTransition(archive, inProcess, "NEW_COPY");
        final JpaRepositoryTransition inactive = new JpaRepositoryTransition(historical, obsolete, "INACTIVE");
        final JpaRepositoryTransition declined = new JpaRepositoryTransition(pendingApproval, historical, "DECLINED");

        final List<JpaRepositoryTransition> transitions = Arrays.asList(submit, reject, approve, originalCopy, newCopy, inactive, declined);
        transitions.forEach(transition -> transition.setMachineId(FsmConstant.REQUEST_MACHINE_ID));
        transitionRepository.saveAll(transitions);

    }


}
