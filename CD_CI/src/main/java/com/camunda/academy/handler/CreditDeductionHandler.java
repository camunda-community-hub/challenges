package com.camunda.academy.handler;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditDeductionHandler {
  Logger logger = LoggerFactory.getLogger(CreditDeductionHandler.class);

  @JobWorker(type = "credit-deduction", autoComplete = false)
  public void handleCreditDeduction(JobClient client, ActivatedJob job) {
    logger.info("Job handled: " + job.getType());
    System.out.println("creadit-deduction: Job handled: " + job.getType());
    client.newCompleteCommand(job.getKey()).send();
  }
}