# Kube DNS 

We setup kube-dns automatically through a python script to route traffic to the distant cluster based on the namespace. This requires to have different namespaces in each cluster.

## Dns Load balancer

** On region 0 (green-east)**

```shell
$ kubectl config get-contexts
     gke_pierre-yves_us-east1_green-east
$ kubectl config use-context gke_pierre-yves_us-east1_green-east
 
$ kubectl apply -f zeebegateway-loadbalancer.yaml  -n kube-system
$ kubectl apply -f dns-lb.yaml  -n kube-system
$ kubectl get svc -n kube-system
kube-dns-lb            LoadBalancer   34.118.235.184   34.26.200.157   53:30624/UDP    22h
```
** On region 1 (blue-west)**

same command.
```shell
$ kubectl config use-context gke_pierre-yves_us-east1_blue-west

$ kubectl apply -f zeebegateway-loadbalancer.yaml -n kube-system
$ kubectl apply -f dns-lb.yaml -n kube-system
$ kubectl get svc -n kube-system
kube-dns-lb            LoadBalancer   34.118.235.184   35.237.32.136   53:30624/UDP    22h
```

Get the list of IP

| region     | Public IP      |
|------------|----------------| 
| blue-west  | 34.26.200.157  |
| green-east | 35.237.32.136  |

## Register each other DNS

**BLUE west cluster**
In the `blue-west`, the `green-east` must be registered.

Execute
```shell
$ kubectl edit configmap kube-dns -n kube-system
```

Set the file:

``` 
data:
  stubDomains: |
    {"green-east.svc.cluster.local": ["35.237.32.136"], "green-east-failover.svc.cluster.local": ["35.237.32.136"]}
```


The file must be
```yaml
apiVersion: v1
data:
  stubDomains: |
    {"green-east.svc.cluster.local": ["34.26.200.157"], "green-east-failover.svc.cluster.local": ["34.26.200.157"]}
kind: ConfigMap
metadata:
  creationTimestamp: "2025-11-19T16:04:52Z"
  labels:
    addonmanager.kubernetes.io/mode: EnsureExists
  name: kube-dns
  namespace: kube-system
  resourceVersion: "1763596440927791001"
  uid: 7b874140-f6ac-46f4-9bba-9e35442964a3
```

Restart the service

```shell
$ kubectl rollout restart deployment kube-dns -n kube-system
```



**GREEN east cluster**
In the `green-east`, the `green-east` must be registered.

Execute
```shell
$ kubectl edit configmap kube-dns -n kube-system
```

Add the file:

``` 
data:
  stubDomains: |
    {"blue-west.svc.cluster.local": ["34.26.200.157"], "blue-west-failover.svc.cluster.local": ["34.26.200.157"]}
```


At the end the file must be

```yaml
apiVersion: v1
data:
  stubDomains: |
    {"green-east.svc.cluster.local": ["34.168.93.206"], "green-east-failover.svc.cluster.local": ["34.168.93.206"]}
kind: ConfigMap
metadata:
  creationTimestamp: "2025-09-22T17:37:45Z"
  labels:
    addonmanager.kubernetes.io/mode: EnsureExists
  name: kube-dns
  namespace: kube-system
  resourceVersion: "1758568538175839001"
  uid: 0f26ea0e-6f6a-4849-bacd-82571b23fefd
```

Restart the service

```shell
$ kubectl rollout restart deployment kube-dns -n kube-system
```
