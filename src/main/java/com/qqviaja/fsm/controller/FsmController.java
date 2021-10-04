package com.qqviaja.fsm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Create on 2021/10/2.</p>
 *
 * @author Kimi Chen
 */
@RestController
public class FsmController {

    @GetMapping("/states")
    public String index(){
        return "This is FSM Demo Project.";
    }
}
