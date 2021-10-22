package com.qqviaja.fsm;

import com.qqviaja.fsm.dao.Request;
import com.qqviaja.fsm.dao.RequestRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackageClasses = RequestRepository.class)
@EntityScan(basePackageClasses = Request.class)
@SpringBootApplication
public class FsmApplication {

    public static void main(String[] args) {
        SpringApplication.run(FsmApplication.class, args);
    }

}
