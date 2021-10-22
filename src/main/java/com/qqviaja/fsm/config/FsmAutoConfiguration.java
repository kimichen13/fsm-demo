package com.qqviaja.fsm.config;

import com.qqviaja.fsm.dao.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.data.*;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * <p>Create on 2021/10/2.</p>
 *
 * @author Kimi Chen
 */
@Configuration
public class FsmAutoConfiguration {


    @EnableStateMachineFactory
    public static class StateMachineConfiguration extends StateMachineConfigurerAdapter<String, String> {

        @Autowired
        private StateRepository<? extends RepositoryState> stateRepository;
        @Autowired
        private TransitionRepository<? extends RepositoryTransition> transitionRepository;

        @Override
        public void configure(StateMachineModelConfigurer<String, String> model) throws Exception {
            model.withModel().factory(modelFactory());
        }

        @Bean
        public StateMachineModelFactory<String, String> modelFactory() {
            return new RepositoryStateMachineModelFactory(stateRepository, transitionRepository);
        }
    }

    @Configuration
    public static class FsmServiceConfiguration {

        @Autowired
        private StateMachineFactory<String, String> stateMachineFactory;

        @Bean
        public StatefulStateMachineService<String, String, Integer, Request> stateMachineService() {
            return new DefaultStatefulStateMachineService<>(stateMachineFactory, inMemoryPersist());
        }

        @Bean
        public InMemoryPersist inMemoryPersist() {
            return new InMemoryPersist();
        }

    }

}
