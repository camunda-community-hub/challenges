package com.camunda.academy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScoreApplication {
    private static final Logger logger = LoggerFactory.getLogger(ScoreApplication.class);


    public static void main(String[] args) {
        logger.info("Logger: This is an info log!");
        SpringApplication.run(ScoreApplication.class, args);
    }
}