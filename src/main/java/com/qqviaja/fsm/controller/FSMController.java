package com.qqviaja.fsm.controller;

import com.qqviaja.fsm.enums.States;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Create on 2021/10/5.</p>
 *
 * @author Kimi Chen
 */
@RestController
public class FSMController {

    @GetMapping("/states")
    public List<String> states() {
        return Arrays.stream(States.values()).map(States::name).collect(Collectors.toList());
    }

}
