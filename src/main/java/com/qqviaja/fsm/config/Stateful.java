package com.qqviaja.fsm.config;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
public interface Stateful<ID> {

    ID getId();

    String getState();

    String getMachineId();

}
