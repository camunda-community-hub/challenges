# Multi-regions

Create a multi regions cluster. Multi region can be two or more regions.

Different architecture are possible.

## Solutions

| Cloud | Approach | Guide |
|---|---|---|
| Azure (AKS) | CoreDNS proxy + static public IPs | [solution/azure/coredns/Solution.md](solution/azure/coredns/Solution.md) |
| Google (GKE) | KubeDNS stub domains + VPC-native networking | [solution/google/kubedns/KubeDns.md](solution/google/kubedns/KubeDns.md) |

# Dual region
Visit (https://docs.camunda.io/docs/self-managed/concepts/multi-region/dual-region/)
The Dual region implies to have
a cluster over multiple region
Each region host ElasticSearch. Then each region are self content. If a region die, other region contains all data.
The cluster must define multiple exporter

Note: this architecture works for two, or multiple regions.

# Centralize ElasticSearch
Instead of having one ElasticSearch per region, one can stand for all regions. Only one exporter is needed. The centralized ElasticSearch is in charge to be robust if one region die.



