package io.camunda.score.worker;


import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties()
public class GetScoreWorker {

    Logger logger = LoggerFactory.getLogger(GetScoreWorker.class.getName());


    @JobWorker(type = "get-score", autoComplete = false)
    public void getScore(final JobClient client, final ActivatedJob job, @Variable(name = "customerId") String customerId) {
        // Calculate the score from the customerId
        String digitsOnly = customerId.replaceAll("\\D+", ""); // \\D = non-digit
        int score = Integer.parseInt(digitsOnly);
        logger.info("GetScoreWorker: customerId [{}] score: {} element[{}]", customerId, score, job.getElementId());
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            logger.error("Ask to interrupt the sleep");
            Thread.currentThread().interrupt();

        }

        client.newCompleteCommand(job.getKey())
                .variables(Map.of("score", score))
                .send().join();
    }

}
