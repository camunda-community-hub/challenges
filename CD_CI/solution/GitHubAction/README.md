# GitHub action

In this folder are grouped the different actions, using GitHub.

The `bpmn` folder creates action to deploy BPMN resources on the Camunda Cluster ([Saas](bpmn/GitHubActionBPMNSaaS.md) or [Self Manage](bpmn/GitHubActionBPMNSelfManage.md))

The `worker`'s folder gives actions used to build a new Docker image from the repository, and deploy it on a cluster
[Worker Deployment](worker/GitHubActionWorkerSelfManage.md)

Last but not the least, the `Process-execution-automator` folder gives actions to deploy and execute scenarii: [UnitTest.md](process-execution-automator/UnitTest.md)
