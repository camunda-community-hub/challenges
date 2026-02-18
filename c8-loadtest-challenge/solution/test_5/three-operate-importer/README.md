## Generic

```
kubectl create namespace camunda
```

```
helm install camunda camunda/camunda-platform -f values.yaml -n camunda
```

## Importer Specific 
```
kubectl get deploy camunda-operate  -n camunda -o yaml > operate-deployment.yaml
```
```
yq eval 'del(.status, .metadata, .spec.template.metadata.annotations, .spec.template.metadata.creationTimestamp,  .spec.selector)' -i operate-deployment.yaml
```
```
yq eval '.metadata.name = "operate-importer-0" | .spec.selector.matchLabels.importer = "operate-importer" | .spec.template.metadata.labels.importer = "operate-importer"' -i operate-deployment.yaml
```

adjust following envs:

```
- name: CAMUNDA_OPERATE_IMPORTERENABLED
  value: "true"
- name: CAMUNDA_OPERATE_ARCHIVERENABLED
  value: "false"
- name: CAMUNDA_OPERATE_WEBAPPENABLED
  value: "false"
- name: CAMUNDA_OPERATE_IMPORTER_THREADSCOUNT
  value: "20"
- name: CAMUNDA_OPERATE_IMPORTER_READERTHREADSCOUNT
  value: "20"
- name: CAMUNDA_OPERATE_CLUSTERNODE_NODECOUNT
  value: "3"
- name: CAMUNDA_OPERATE_CLUSTERNODE_CURRENTNODEID
  value: "0"
```

copy the generated deployment file 3 times. Now adjust CAMUNDA_OPERATE_CLUSTERNODE_CURRENTNODEID and .metadata.name accordingly.

```
kubectl apply -f operate-importer-0.yaml -n camunda
```
Logs: 
```
2024-04-17 17:15:19.229  INFO 7 --- [           main] i.c.o.z.RecordsReaderHolder              : Starting import for partitions: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
```

```
kubectl apply -f operate-importer-1.yaml -n camunda
```
Logs:
```
2024-04-17 17:17:02.546  INFO 7 --- [           main] i.c.o.z.RecordsReaderHolder              : Starting import for partitions: [21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40]
```

```
kubectl apply -f operate-importer-2.yaml -n camunda
```
Logs:
```
Starting import for partitions: [41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60]
```

```
kubectl apply -f importer-service.yaml
```

## Generic

```
kubectl create configmap models --from-file=CamundaProcess.bpmn=BenchmarkProcess.bpmn -n camunda
```

```
kubectl apply -f https://raw.githubusercontent.com/camunda-community-hub/camunda-8-helm-profiles/c09d5f91504302e166f87fb95119f29efadc165f/include/zbctl-deploy-job.yaml -n camunda
```
```
kubectl create configmap payload --from-file=payload.json -n camunda
```

```
kubectl apply -f rebalance.yaml -n camunda
```

```
kubectl apply -f benchmark.yaml -n camunda
```