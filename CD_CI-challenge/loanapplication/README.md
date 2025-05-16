# c8-automate-test
This project explain how to add and automate test on the LoadApplication project based on the GitHub Action

The different explanation is given here https://github.com/camunda-community-hub/challenges/tree/main/CD_CI/solution/GitHubAction


# JUnit test
JUnit test is run when a maven install is performed.
It's possible to run it manually. 
Unit test used a Zeebe In Memory engine

# Automatic deployment on a cluster


## BPMN file
The [.github/workflows/sm-deploy-bpmn.yaml](.github/workflows/sm-deploy-bpmn.yaml) deploy automatically all BPMN process. 
This is based on secret variable setup to identify the cluster to use to deploy.

## Build image and deploy it
Actions [.github/workflows/1-worker-build-publish-image.yaml](.github/workflows/1-worker-build-publish-image.yaml)
and
[.github/workflows/2-worker-deploy-on-cluster.yaml](.github/workflows/2-worker-deploy-on-cluster.yaml)

create an image, and deploy them in the cluster. 

## Automate test 

The action [3-pea-loadscenarii.yaml](.github/workflows/3-pea-loadscenarii.yaml) loads all scenario, and [4-pea-run-scenarii.yaml](.github/workflows/4-pea-run-scenarii.yaml)
run them.
The result is accessible via a REST Call or via the Pea UI

