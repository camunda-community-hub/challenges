# Multi Region disaster

Start a multi region cluster, and run a load test.

Stop the region 1.

Using the replication factor downsize method, allow the cluster to continue. 
Operate, Tasklist will not show any new tasks.

Restart the region 1. 

Using the replication factor upgrade, re-start the initial configuration.

Check the [Solution](solution/README.md) for the detailed procedure
