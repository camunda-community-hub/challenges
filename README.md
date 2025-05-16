# 🚀 Camunda 8 Challenge Project


Welcome to the **Camunda 8 Challenge Project**!  
This repository contains a series of hands-on challenges designed to help you explore and master different features of the Camunda 8 platform.

Each challenge includes step-by-step instructions and real-world scenarios to build your skills.

---

## 🧩 Challenges Overview

### 🔄 Backup & Restore

Learn how to work with Camunda 8's backup and restore functionality:

- Configure the platform for scheduled or manual backups.
- Create and verify your first backup.
- Prepare Kubernetes manifests for the restore process.
- Perform a full restore and validate the recovery.

Go to [Backup and restore](Backup-Restore-challenge/README.md) challenge.

---

### 🤖 AI Challenge

Explore Camunda 8’s AI-powered features:

- Use the AI assistant to generate BPMN processes and user forms.
- Connect to **Mistral** to estimate costs (e.g., for car or boat repairs).
- Connect to a S3 storage to saved document uploaded by the user (use the new document management feature)
- See how generative AI accelerates development.

Visit [C8-AI-challenge](c8-AI-challenge/README.md)

---

### 📊 Load Testing

Camunda 8 supports scaling through partitioned clusters — but how does it behave under stress?

- Start from a customer workload specification.
- Calibrate the cluster setup (e.g., number of partitions).
- Use the Camunda load testing tool to simulate traffic.
- Monitor the system using **Grafana** dashboards.
- Tune parameters and rerun the test.
- Analyze results, validate **Operate** performance, optimize **Elasticsearch**, and estimate **PVC** storage needs.

Jump to the [Load Test](c8-loadtest-challenge/README.md) and learn!

---

### ⚙️ CI/CD Challenge

Implement a robust **CI/CD pipeline** for Camunda 8:

- Support deployment in both **SaaS** and **Self-Managed** environments.
- Automate deployment of BPMN processes and workers.
- Integrate tests to run nightly or on push.
- Ensure platform consistency across all environments.

Visit immediately the [CD CI](CD_CI-challenge/README.md) path


---

## 📁 Folder Structure

