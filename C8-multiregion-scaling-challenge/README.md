# Multi region scaling challenge

Create a Dual Architecture on two region, on a cloud (Google).
Initial parameters:

| parameter          | Value                     |
|--------------------|---------------------------|
| Cluster Sier       | 4 (2 pods on each region) |
| Replication factor | 4                         |
| Partition          | 3                         |  


* Change the number of partition to 4

* Scale up the cluster to have 3 pod on each region (cluster size=6)

* Scale down the cluster to have 2 pods on each region (cluster size=4)


Solution are available [solution/README.md](solution/README.md)
