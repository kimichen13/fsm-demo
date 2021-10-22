package com.qqviaja.fsm.service;

import com.qqviaja.fsm.dao.Request;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
public interface IFsmService {

    Request createRequest();

    Request execute(Integer requestId, String event);
}
