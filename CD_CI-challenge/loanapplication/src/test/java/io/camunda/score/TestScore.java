package io.camunda.score;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.assertions.DeploymentAssert;
import io.camunda.zeebe.process.test.assertions.ProcessInstanceAssert;
import io.camunda.zeebe.protocol.Protocol;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.camunda.community.process_test_coverage.junit5.platform8.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceHasPassedElement;

@SpringBootTest
@ZeebeSpringTest

@ExtendWith(ProcessEngineCoverageExtension.class)

public class TestScore {

    Logger logger = LoggerFactory.getLogger(TestScore.class.getName());

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ZeebeTestEngine engine;

    // @Autowired
    // private CamundaProcessTestContext processTestContext;

    public void deployResource(String resourceId) {
        logger.info("Deploy process [{}]", resourceId);
        DeploymentEvent event = zeebeClient.newDeployResourceCommand()
                .addResourceFromClasspath(resourceId)
                .send()
                .join();
        DeploymentAssert assertions = BpmnAssert.assertThat(event);

    }

    @Test
    void acceptedLoan() {
        deployResource("LoanApplication.bpmn");

        ProcessInstanceEvent processInstance =
                zeebeClient
                        .newCreateInstanceCommand() //
                        .bpmnProcessId("LoanApplication")
                        .latestVersion() //
                        .variables(Map.of("customerId", "Customer987")) //
                        .send()
                        .join();
        BpmnAssert.assertThat(processInstance);

        waitForProcessInstanceCompleted(processInstance);

        // Let's assert that it passed certain BPMN elements (more to show off features here)
        ProcessInstanceAssert processInstanceAssert = BpmnAssert.assertThat(processInstance);
        processInstanceAssert.isCompleted();
        processInstanceAssert.hasVariableWithValue("score", 987);
        processInstanceAssert.hasPassedElement("GetScore");
        processInstanceAssert.hasPassedElement("AcceptLoan");
        logger.info("Accepted loan passed with success");
    }

    @Test
    void rejectLoan() {
        deployResource("LoanApplication.bpmn");

        ProcessInstanceEvent processInstance =
                zeebeClient
                        .newCreateInstanceCommand() //
                        .bpmnProcessId("LoanApplication")
                        .latestVersion() //
                        .variables(Map.of("customerId", "Customer120")) //
                        .send()
                        .join();
        BpmnAssert.assertThat(processInstance);

        waitForProcessInstanceCompleted(processInstance);

        // Let's assert that it passed certain BPMN elements (more to show off features here)
        ProcessInstanceAssert processInstanceAssert = BpmnAssert.assertThat(processInstance);
        processInstanceAssert.isCompleted();
        processInstanceAssert.hasVariableWithValue("score", 120);
        processInstanceAssert.hasPassedElement("GetScore");
        processInstanceAssert.hasPassedElement("RejectLoan");
        logger.info("Reject loan passed with success");
    }

    @Test
    void acceptManualLoan() {
        deployResource("LoanApplication.bpmn");
        deployResource("LoanCheck.form");

        ProcessInstanceEvent processInstance =
                zeebeClient
                        .newCreateInstanceCommand() //
                        .bpmnProcessId("LoanApplication")
                        .latestVersion() //
                        .variables(Map.of("customerId", "Customer500")) //
                        .send()
                        .join();
        BpmnAssert.assertThat(processInstance);

        waitForProcessInstanceHasPassedElement(processInstance.getProcessInstanceKey(), "GatewayCheckLoanAcceptance");
        waitForUserTaskAndComplete("CheckManualLoan", Map.of("acceptLoan", Boolean.TRUE));
        waitForProcessInstanceCompleted(processInstance);

        // Let's assert that it passed certain BPMN elements (more to show off features here)
        ProcessInstanceAssert processInstanceAssert = BpmnAssert.assertThat(processInstance);
        processInstanceAssert.isCompleted();

        processInstanceAssert.hasPassedElement("GetScore");
        processInstanceAssert.hasPassedElement("AcceptLoan");
        logger.info("Accept Manual loan passed with success");
    }
    @Test
    void rejectManualLoan() {
        deployResource("LoanApplication.bpmn");
        deployResource("LoanCheck.form");

        ProcessInstanceEvent processInstance =
                zeebeClient
                        .newCreateInstanceCommand() //
                        .bpmnProcessId("LoanApplication")
                        .latestVersion() //
                        .variables(Map.of("customerId", "Customer500")) //
                        .send()
                        .join();
        BpmnAssert.assertThat(processInstance);

        waitForProcessInstanceHasPassedElement(processInstance.getProcessInstanceKey(), "GatewayCheckLoanAcceptance");
        waitForUserTaskAndComplete("CheckManualLoan", Map.of("acceptLoan", Boolean.FALSE));
        waitForProcessInstanceCompleted(processInstance);

        // Let's assert that it passed certain BPMN elements (more to show off features here)
        ProcessInstanceAssert processInstanceAssert = BpmnAssert.assertThat(processInstance);
        processInstanceAssert.isCompleted();

        processInstanceAssert.hasPassedElement("GetScore");
        processInstanceAssert.hasPassedElement("AcceptLoan");
        logger.info("Reject Manual loan passed with success");
    }

    private void waitForUserTaskAndComplete(String userTaskId, Map<String, Object> variables) {
        // Let the workflow engine do whatever it needs to do
        try {
            engine.waitForIdleState(Duration.ofMinutes(1));
        } catch (Exception e) {
            logger.error("Interruption during wait ", e);
        }
        // Now get all user tasks
        List<ActivatedJob> jobs = zeebeClient.newActivateJobsCommand().jobType(Protocol.USER_TASK_JOB_TYPE).maxJobsToActivate(1).send().join().getJobs();

        // Should be only one
        Assert.isTrue(jobs.size() > 0, "Job for user task '" + userTaskId + "' does not exist");
        ActivatedJob userTaskJob = jobs.get(0);
        // Make sure it is the right one
        if (userTaskId != null) {
            Assert.isTrue(userTaskId.equals(userTaskJob.getElementId()), "Expected task");
        }

        // And complete it passing the variables
        if (variables != null) {
            zeebeClient.newCompleteCommand(userTaskJob.getKey()).variables(variables).send().join();
        } else {
            zeebeClient.newCompleteCommand(userTaskJob.getKey()).send().join();
        }
    }
}
