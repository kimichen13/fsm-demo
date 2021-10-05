package com.qqviaja.fsm.controller;

import com.qqviaja.fsm.enums.Events;
import com.qqviaja.fsm.enums.States;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Create on 2021/10/2.</p>
 *
 * @author Kimi Chen
 */
@RestController
public class FsmController {

    @GetMapping("/states")
    public List<String> states() {
        return Arrays.stream(States.values()).map(States::name).collect(Collectors.toList());
    }

    @GetMapping("/events")
    public List<String> events() {
        return Arrays.stream(Events.values()).map(Events::name).collect(Collectors.toList());
    }
}
