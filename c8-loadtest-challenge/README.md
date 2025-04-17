# c8-loadtest-challenge
Load test challenge: from a specification, identify the correct cluster configuration.

# Specification

Check the [Specification. md](doc/Specification.md) file to see the target. Your goal is to set up a cluster that can handle this load.

The specification is complex and does not have a very high throughput. This example will help you practice the tooling, understand the bottleneck, and adjust the configuration.
# Tooling
Set up a load environment. It would help if you chose a way to simulate the load. You have different options:
* Develop your application. Create a software-injecting process instance
* Use the https://github.com/camunda-community-hub/camunda-8-benchmark tool
* Use the https://github.com/camunda-community-hub/process-execution-automator tool

# Methodology
Estimate the first cluster: The best method is to calculate the number of service tasks per second and divide by 150.
You have several partitions. It is a good start.
Attention: the more workers you have, the more partitions you need.

Set up the Grafana page with all metrics. It is mandatory to know what needs to be fixed. Do you face backpressure? Do you handle the creation throughput?

Operate is also needed. With Operate, you can see which worker is the bottleneck and scale accordingly.

Run a test, check the result, and change only one parameter at a time, except if you are fair to the goal and see multiple adjustments to perform.

# tips

When you resolve backpressure and other errors but still are not at the expected throughput, check Operate.
Wait some time and see where many tasks are stuck: scale this worker.

At the end, stop the workers and injection. Then, check 'operate '. After one minute, verify if the values have changed. If they have, it indicates that 'operate import' is not following the expected throughput.

You will need to scale operate.

The application allows this scaling but not the Helm chart. You will have to deactivate Operate in the Helm chart and configure it manually.

You have:
* To start multiple Importer Pod (importerEnabled:True and archiverEnable:False, webAppEnabled: False)
* one web app pod
* multiple archiver pod

Check:
https://docs.camunda.io/docs/next/self-managed/operate-deployment/importer-and-archiver/

# Solution

A solution path is given under the folder "solution".



