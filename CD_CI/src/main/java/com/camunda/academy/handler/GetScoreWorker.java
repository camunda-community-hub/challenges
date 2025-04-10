package com.camunda.academy.handler;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CreditCardChargingHandler {

  Logger logger = LoggerFactory.getLogger(CreditCardChargingHandler.class);

  @JobWorker(type = "get-score", autoComplete = false)
  public void handleCreditDeduction(JobClient client, ActivatedJob job, @Variable ) {
    logger.info("Job handled: " + job.getType());
    client.newCompleteCommand(job.getKey()).send();
  }
}
