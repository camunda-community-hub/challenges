# Multi-region scaling

The multi region cluster is created on Google

See the challenge "[Set up a multi region](../../c8-multi-region/solution/README.md)




# Check health

1. Port forward port 8080; 9600 and 26500 

Technically, 26500 is needed only if you want to deploy a BPMN process and start a process instance

```shell
$ kubectl config use-context gke_pierre-yves_us-east1_green-east
$ kubectl port-forward -n green-east service/camunda-zeebe-gateway 8080:8080 &
$ kubectl port-forward -n green-east service/camunda-zeebe-gateway 26500:26500 &
$ kubectl port-forward -n green-east service/camunda-zeebe-gateway 9600:9600 &

```

2. Run a re-balance command

Rebalance the cluster

```shell
$ curl  -c cookies.txt -X POST "http://localhost:8080/login" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "username=demo" \
-d "password=demo"

curl -X POST http://localhost:9600/actuator/rebalance
```
3. Run a topology

```shell
$ curl  -c cookies.txt -X POST "http://localhost:8080/login" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "username=demo" \
-d "password=demo"

$ curl -b cookies.txt http://localhost:8080/v2/topology
```

This command return the topology command

````json
{
  "brokers": [
    {
      "nodeId": 1,
      "host": "camunda-zeebe-0.camunda-zeebe.green-east.svc",
      "port": 26501,
      "partitions": [
        {
          "partitionId": 1,
          "role": "follower",
          "health": "healthy"
        },
        {
          "partitionId": 2,
          "role": "follower",
          "health": "healthy"
        },
        {
          "partitionId": 3,
          "role": "follower",
          "health": "healthy"
        }
      ],
      "version": "8.8.3"
    },
    {
      "nodeId": 2,
      "host": "camunda-zeebe-1.camunda-zeebe.blue-west.svc",
      "port": 26501,
      "partitions": [
        {
          "partitionId": 1,
          "role": "follower",
          "health": "hea\r\nlthy"
        },
        {
          "partitionId": 2,
          "role": "follower",
          "health": "healthy"
        },
        {
          "partitionId": 3,
          "role": "follower",
          "health": "healthy"
        }
      ],
      "version": "8.8.3"
    },
    {
      "nodeId": 3,
      "host": "camunda-zeebe-1.camunda-zeebe.green-east.svc",
      "port": 26501,
      "partitions": [
        {
          "partitionId": 1,
          "role": "leader",
          "health": "healthy"
        },
        {
          "partitionId": 2,
          "role": "leader",
          "health": "healthy"
        },
        {
          "partitionId": 3,
          "role": "leader",
          "health": "healthy"
        }
      ],
      "version": "8.8.3"
    },
    {
      "nodeId": 0,
      "host": "c\r\namunda-zeebe-0.camunda-zeebe.blue-west.svc",
      "port": 26501,
      "partitions": [
        {
          "partitionId": 1,
          "role": "follower",
          "health": "healthy"
        },
        {
          "partitionId": 2,
          "role": "follower",
          "health": "healthy"
        },
        {
          "partitionId": 3,
          "role": "follower",
          "health": "healthy"
        }
      ],
      "version": "8.8.3"
    }
  ],
  "clusterSize": 4,
  "partitionsCount": 3,
  "replicationFactor": 4,
  "gatewayVersion": "8.8.3",
  "lastCompletedChangeId": "-1"
}
````

We analyse the cluster

| Parameter  | Value | Status                    |
|------------|-------|---------------------------| 
| Partitions | 5     | 3 partitions as expected  |

Distribution:

| Partition | 0/blue | 1/green | 2/blue   | 3/green |
|-----------|:-------|:--------|:---------|:--------|
| 1         | L      | F       | F        | F      |
| 2         | F      | L       | F        | F      |     
| 3         | L      | F       | F        | F      |
| 4         | F      | L       | F        | F      |
| 5         | L      | F       | F        | F      |


> Leaders may be not very well distributed, due to the lattency on startup between region.
 


# Add partitions

Add two new partitions

```shell
curl -X 'PATCH' \
   'http://localhost:9600/actuator/cluster' \
   -H 'accept: application/json' \
   -H 'Content-Type: application/json' \
   -d '{
        "partitions": {
          "count": 7
        }
      }'
```
In the result, check the `plannedChanges` section
```json
{
"operation": "START_PARTITION_SCALE_UP",
"brokerId": 0,
"brokers": []
}
```

then query the status
```shell
curl --request GET 'http://localhost:9600/actuator/cluster'
```


Execute again a topology:

```shell
$ curl -b cookies.txt http://localhost:8080/v2/topology
```

| Partition | 0/blue | 1/green | 2/blue | 3/green |
|-----------|:-------|:--------|:-------|:--------|
| 1         | L      | F       | F      | F       |
| 2         | F      | L       | F      | F       |     
| 3         | L      | F       | F      | F       |
| 4         | F      | L       | F      | F       |
| 5         | L      | F       | F      | F       |
| 6         | F      | L       | F      | F       |
| 7         | F      | F       | L      | F       |



# Scale up

two new brokers will be added in the cluster, one in each region

1. Scale the first region

```shell
$ kubectl config use-context gke_pierre-yves_us-east1_blue-west
$ kubectl scale statefulset camunda-zeebe --replicas=3
```

> The new pod is created and will not be "ready". it will be ready when it will be added in the cluster via the REST API.
 
> It's a good idea to update the YAML with the new version if the scale is definitif, in order to have a value.yaml synchronized with the cluster. It's possible to use a helm upgrade to scale the cluster too.

**Upgrade the value.yaml (not mandatory)**

Add in both value.yaml the corresponding initial contact point, pod 3 and 4

``
    - name: ZEEBE_BROKER_CLUSTER_INITIALCONTACTPOINTS
      value: "camunda-zeebe-0.camunda-zeebe.green-east.svc.cluster.local:26502,
      camunda-zeebe-1.camunda-zeebe.green-east.svc.cluster.local:26502,
      camunda-zeebe-2.camunda-zeebe.green-east.svc.cluster.local:26502,  
      camunda-zeebe-0.camunda-zeebe.blue-west.svc.cluster.local:26502,
      camunda-zeebe-1.camunda-zeebe.blue-west.svc.cluster.local:26502,
      camunda-zeebe-2.camunda-zeebe.blue-west.svc.cluster.local:26502"
``

Move the cluster size to 6

```yaml
orchestration:
  clusterSize: "6"
```

2. Scale the second region

```shell
$ kubectl config use-context gke_pierre-yves_us-east1_green-east
$ kubectl scale statefulset camunda-zeebe --replicas=3
```

At this moment, both cluster contains 3 pods, but the last one is Running / Not ready.

```shell

$ kubectl get pods
NAME                             READY   STATUS    RESTARTS   AGE
camunda-elasticsearch-master-0   1/1     Running   0          30m
camunda-zeebe-0                  1/1     Running   0          30m
camunda-zeebe-1                  1/1     Running   0          30m
camunda-zeebe-2                  0/1     Running   0          2m55s
```



3. Add brokers in the Camunda cluster


```shell
$ curl -X 'PATCH' \
  'http://localhost:9600/actuator/cluster' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
       "brokers": {
         "add": [4,5]
       }
     }'
```

Using the actuator request to get the status of progress

```shell
curl --request GET http://localhost:9600/actuator/cluster
```

Result is under `pendingChange`, under section `completed` and `pending`

```
 "pendingChange": {
    "id": 2,
    "status": "IN_PROGRESS",
    "completed": [
      {
        "operation": "BROKER_ADD",
        "brokerId": 4,
        "brokers": [],
        "completedAt": "2025-11-21T23:28:13.000+0000"
      },
      {
        "operation": "BROKER_ADD",
        "brokerId": 5,
        "brokers": [],
        "completedAt": "2025-11-21T23:28:33.000+0000"
      }
    ],
    "pending": [
      {
        "operation": "PARTITION_JOIN",
        "brokerId": 4,
        "partitionId": 2,
        "priority": 1,
        "brokers": []
      },

```


After a moment, all changes are applied


Execute again a topology:
```shell
$ curl -b cookies.txt http://localhost:8080/v2/topology
```


| Partition | 0/blue | 1/green | 2/blue | 3/green | 4/blue | 5/green |
|-----------|:-------|:--------|:-------|:--------|--------|---------|
| 1         | L      | F       | F       | F      |         |        |
| 2         |        | L       | F       | F      | F       |        |
| 3         |        |         | L       | F      | F       | F      |
| 4         | F      |         |         | L      | F       | F      |
| 5         | L      | F       |         |        | F       | F      |
| 6         | F      | L       | F       |        |         | F      |
| 7         | F      | F       | L       | F      |         |        |

# Scale down

Remove brokers in the Camunda cluster


```shell
$ curl -X 'PATCH' \
  'http://localhost:9600/actuator/cluster' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
       "brokers": {
         "remove": [4,5]
       }
     }'
```

Using the actuator request to get the status of progress

```shell
curl --request GET http://localhost:9600/actuator/cluster
```

When no pending change are present, run a topology command


Execute again a topology:
```shell
$ curl -b cookies.txt http://localhost:8080/v2/topology
```


| Partition | 0/blue  | 1/green | 2/blue   | 3/green | 4/blue | 5/green |
|-----------|:--------|:--------|:---------|:--------|--------|---------|
| 1         | L       | F       | F        | F       |        |         |
| 2         | F       | L       | F        | F       |        |         | 
| 3         | F       | F       | L        | F       |        |         | 
| 4         | F       | F       | F        | L       |        |         | 
| 5         | L       | F       | F        | F       |        |         | 
| 6         | F       | L       | F        | F       |        |         | 
| 7         | F       | F       | L        | F       |        |         | 


At this moment, it's possible to scale down brokers on both cluster


```shell
$ kubectl config use-context gke_pierre-yves_us-east1_blue-west
$ kubectl scale statefulset camunda-zeebe --replicas=2
```
```shell
$ kubectl config use-context gke_pierre-yves_us-east1_green-east
$ kubectl scale statefulset camunda-zeebe --replicas=2
```
# Uninstall

```shell
$ kubectl config use-context gke_pierre-yves_us-east1_blue-west
$ helm uninstall --namespace blue-west camunda

$ kubectl config use-context gke_pierre-yves_us-east1_green-east
$ helm uninstall --namespace green-east camunda

```
