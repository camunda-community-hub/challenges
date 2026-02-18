# 🧩 Solution: Step-by-Step Installation with Datadog

This guide provides a step-by-step walkthrough for setting up **Datadog** in Kubernetes and enabling **Datadog** integration for collecting metrics from Camunda (Zeebe) via Micrometer.

---
## 🪪 Step 0: Get Your Datadog API Key

1. Log in to your Datadog account at [https://app.datadoghq.com](https://app.datadoghq.com) (You should have a 14 days trial)
2. Go to **Organization Settings → API Keys**
3. Copy your existing API key, or **create a new one** if needed.
4. You’ll use this API key when creating the Kubernetes secret in the next step.

## 🪜 Step 1: Add Helm Repository and Install Operator

```bash
# Add Datadog Helm repository
helm repo add datadog https://helm.datadoghq.com
helm repo update

# Install the Datadog Operator
helm install datadog-operator datadog/datadog-operator
```

## 🔑 Step 2: Create the Secret

Create a Kubernetes secret to store your Datadog API key.
```bash
# Create the secret with your API key
kubectl create secret generic datadog-secret --from-literal api-key=<your-data-dog-api-key>
```
## ⚙️ Step 3: Create the DatadogAgent Configuration
Create a file named datadog-agent.yaml with the following content:
```bash
apiVersion: datadoghq.com/v2alpha1
kind: DatadogAgent
metadata:
  name: datadog
spec:
  global:
    site: us5.datadoghq.com
    credentials:
      apiSecret:
        secretName: datadog-secret
        keyName: api-key # your datadog api-secret-key

  features:
    dogstatsd:
      originDetectionEnabled: false

    logCollection:
      enabled: true
      containerCollectAll: true

    apm:
      enabled: true

  override:
    clusterAgent:
      containers:
        cluster-agent:
          env:
            - name: DD_ADMISSION_CONTROLLER_ADD_AKS_SELECTORS
              value: "true"
```

## 🚀 Step 4: Apply the DatadogAgent Configuration
```bash
# Apply the configuration
kubectl apply -f datadog-agent.yaml
```

## 🔍 Step 5: Verify Installation
```bash
# Wait for the operator to create the Datadog agent
kubectl get datadogagent

# Check the pods
kubectl get pods -l app.kubernetes.io/instance=datadog

# You should see pods like:
# - datadog-agent-xxxxx (node agents, one per node)
# - datadog-cluster-agent-xxxxx (cluster agent)
# Check your datadog-agent port Containers.agent.Port (usually 8125/UDP).
# You would need the port number for your Camunda helm values.yaml
```
Example:
![statsD-dataDogk8s.png](../images/statsD-dataDogk8s.png)