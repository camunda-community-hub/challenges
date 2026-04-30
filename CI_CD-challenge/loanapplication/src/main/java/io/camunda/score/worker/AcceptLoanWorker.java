package io.camunda.score.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AcceptLoanWorker {

    Logger logger = LoggerFactory.getLogger(AcceptLoanWorker.class.getName());

    @JobWorker(type = "accept-loan", autoComplete = false)
    public void acceptLoan(final JobClient client, final ActivatedJob job, @Variable(name = "customerId") String customerId) {
        // Calculate the score from the customerId
        logger.info("AcceptLoan: customerId [{}] element [{}]", customerId,job.getElementId());
        try {
            Thread.sleep(270);
        } catch (InterruptedException e) {
            logger.error("Ask to interrupt the sleep");
            Thread.currentThread().interrupt();
        }

        client.newCompleteCommand(job.getKey())
                .send().join();
    }

}
