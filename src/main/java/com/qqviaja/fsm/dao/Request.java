package com.qqviaja.fsm.dao;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;
import com.qqviaja.fsm.common.FsmConstant;
import com.qqviaja.fsm.config.Stateful;
import lombok.*;

import javax.persistence.*;

/**
 * <p>Create on 2021/10/21.</p>
 *
 * @author Kimi Chen
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Request")
@JsonIdentityInfo(generator = IntSequenceGenerator.class)
public class Request implements Stateful<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String state;

    @Transient
    private String machineId = FsmConstant.REQUEST_MACHINE_ID;

}
