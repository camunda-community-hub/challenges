package com.camunda.academy.worker;

import com.google.common.collect.Maps;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetScoreWorker {

  Logger logger = LoggerFactory.getLogger(GetScoreWorker.class);

  @JobWorker(type = "get-score", autoComplete = false)
  public void handleCreditDeduction(JobClient client, ActivatedJob job, @Variable Integer numberOfCreditCards) {
    logger.info("Job handled: " + job.getType());
    int score= 700 ;
    if (numberOfCreditCards> 3)
      score=900;
    client.newCompleteCommand(job.getKey())
            .variables(Map.of("score", score))
            .send();
  }
}
