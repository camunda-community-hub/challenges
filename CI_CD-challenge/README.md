# CD CI
This challenge is to build a CD-CI

# C8-cd-ci-challenge

The goal of this challenge is to build a CD on a Self Manage and a SaaS.

Then, second, a CI can be set up.

# Preparation

Create a GitHub repository, and use the content of the directory `loadapplication`
The repository contains:
* src/main/java contains the code for the worker.
* src/main/resources/RiskCalculation.bpmn: the process to deploy
* src/main/resources/RiskCalculation.dmn : the DMN table used in the process
* pom.xml is the pom used to compile the project, which contains a worker
* Dockerfile is used to rebuild the image


The Result is
![img.png](doc/LocalGitRepository.png)


Option 1: Start a Self Manage cluster.

Option 2: Use the SaaS connection


Check the different [solution](solution/solution.md) here.

# Chapter 1: BPMN Continuous deployment

When you push a new version of a BPMN process, or a new process, this process is deployed on the server.

# Chapter 2: Worker Continuous deployment


A worker is part of the GitHub under `src/main/java` . When a piece of code is pushed, then the environment
* compile the new version and create a JAR file. The jar car be saved under the GitHub release.
* deploy it on a Connector runtime (maybe the Connector Runtime or Cherry Runtime)


# Chapter 3: Continuous Integration


When something changed (new process, new worker), tests must run on it.

The Process Execution Automator (PEA) is used to run scenarii on the platorm.

The Process Execution Automator (PEA) must be deployed first somewhere.
The action will 
* deploy any PEA scenario
* ask Process Execution Automator (PEA) to execute them


# Chapter 4: use different branches

The GitHub repository contains two branches: main and development name `chicago`
The `chigago` contains a GitHub deployment to push on the validation project.
Web Modeler is connected to `chicago`. At a moment, development is finish, and the version is merge in the `main`.

| Solution      | Self Manage |
|---------------|-------------|
| GitHub action |             |
| Jenkins       |             |                                                                         
| Process       |             |                                                                         
