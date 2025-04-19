package io.camunda.score;

import io.camunda.score.worker.GetScoreWorker;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

public class LoanApplication {
    private static final Logger logger = LoggerFactory.getLogger(LoanApplication.class);



    public static void main(String[] args) {
        logger.info("Starting application");
        SpringApplication.run(LoanApplication.class, args);
    }


}