 # Different solution
 
Different solutions are explained here, based on different tools. Each solution explore two targets: a SaaS environment and a Self Manage environment

# Different tool
## GitHub action
A GitHub repository contains an internal mechanism call GitAction. Via this method, an action can be trigger on an event. The event may be a push in the repository, or the creation of a new release.

![CD/CI with GihubAction](images/GitHubAction-CDCI.png)

Two flavors:
* [GitHubActionSaaS](GitHubAction/bpmn/GitHubActionBPMNSaaS.md) to deploy using a SaaS
* [GitHubActionSelfManage](GitHubAction/bpmn/GitHubActionBPMNSelfManage.md) to deploy in a Self manage environment



## Jenkins
Jenkins is a very popular tool to automate operation.

# Preparation
To prepare the challenge, create a Git Repository from the folder, and copy the src directory and the pom.xml file

Create a GitHub project and copy into it
   pom.xml
   src directory

This is the base for all the solution. The goal is to 