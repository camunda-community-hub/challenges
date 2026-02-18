# GitHub action

In this folder are grouped the different actions, using GitHub.


# Chapter 1: BPMN Continuous deployment

The `bpmn` folder creates action to deploy BPMN resources on the Camunda Cluster, in Saas or Self manage

| Solution      | Saas                                                       | Self Manage                                                             |
|---------------|------------------------------------------------------------|-------------------------------------------------------------------------|
| GitHub action | [Saas](bpmn/GitHubActionBPMNSaaS.md) | [Self Manage](bpmn/GitHubActionBPMNSelfManage.md) |

# Chapter 2: Worker Continuous deployment

The `worker`'s folder gives actions used to build a new Docker image from the repository, and deploy it on a cluster



| Solution      | Self Manage                                                                 |
|---------------|-----------------------------------------------------------------------------|
| GitHub action | [SelfManage](worker/GitHubActionWorkerSelfManage.md)  |


# Chapter 3: Continuous Integration

Last but not the least, the `Process-execution-automator` folder gives actions to deploy and execute scenarii: [UnitTest.md](process-execution-automator/UnitTest.md)


| Solution      | Self Manage                                                                     |
|---------------|---------------------------------------------------------------------------------|
| GitHub action | [PEA execution](process-execution-automator/UnitTest.md)  |

# Chapter 4: use different branches

This is implemented based on the trigger on each action.
