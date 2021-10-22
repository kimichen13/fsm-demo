package com.qqviaja.fsm.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
@Repository
public interface RequestRepository extends CrudRepository<Request, Integer> {
}
