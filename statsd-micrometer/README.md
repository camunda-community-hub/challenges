# Specification
This project aims to expose and explore the full observability capabilities of Camunda Platform 8 by integrating with various Micrometer-compatible monitoring systems.
It’s designed to help you understand how different metric collection models work — particularly Prometheus (pull model) and StatsD (push model) — and how Camunda components like Zeebe can be configured to send their operational metrics to these systems.

---
## 🎯 Goals

- Gain hands-on experience configuring **Micrometer** inside Camunda Platform 8.
- Understand how **different monitoring backends** (Prometheus, StatsD, Datadog, etc.) consume metrics.
- Learn the difference between **pull-based** and **push-based** metric collection models.
- Validate metric collection by running **StatsD Exporter** or **Prometheus Server** locally or within Kubernetes.

---

## 🧠 Background

**Micrometer** acts as an abstraction layer for metrics collection in Java-based applications.  
It allows applications to record metrics once and then publish them to various monitoring systems through specific Micrometer “binders.”

Camunda Platform 8 (Zeebe, Operate, Tasklist, etc.) uses Micrometer under the hood to provide runtime metrics such as process execution times, job handling counts, and resource usage.

---

## 🔍 Metric Models Explained

### Prometheus – Pull Model
- Prometheus **pulls (scrapes)** metrics from applications at a specific HTTP endpoint (usually `/actuator/prometheus` or `/metrics`).
- In Camunda’s case, **Zeebe exposes an endpoint** that lists all current metrics in plain text format.
- The **Prometheus server** periodically scrapes that endpoint and stores metrics for querying and visualization (e.g., via Grafana).
---

## ⚙️ Common Monitoring Systems with Micrometer

| System | Model | Description |
|--------|--------|-------------|
| **Prometheus** | Pull | Scrapes exposed HTTP endpoints for metrics. Default in most Kubernetes setups. |
| **StatsD** | Push | Simple daemon that receives metrics over UDP or TCP and forwards them. |
| **Datadog** | Push | Can receive metrics from StatsD or directly from Micrometer via API. |
| **InfluxDB / Graphite** | Push | Popular time-series databases used with custom exporters or agents. |

---

## 🚀 What You’ll Do in This Challenge

1. Deploy **Camunda Platform 8** locally or on Kubernetes.
2. Mount the **Micrometer StatsD JAR** into the Zeebe `/lib` directory using an initContainer or volume mount.
3. Configure Zeebe to use the **Micrometer StatsD registry**.
4. Deploy a **DataDog** to collect and expose metrics.
5. Validate that Zeebe is successfully pushing metrics to StatsD (e.g., by inspecting logs or using a Datadog adapter).

---
## 🧩 Using Other Monitoring Systems
To use a different monitoring system, refer to the [**Spring Boot**](https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.export) documentation. Zeebe only shps with built-in support for the [Prometheus](https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.export.prometheus) and [OTLP](https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.export.otlp) systems.

To use a different system, you must add the required dependencies to your Zeebe installation, specifically to the distribution's lib/ folder.

When using the container image, you must add it to the following paths, based on your image:

* camunda/zeebe: /usr/local/zeebe/lib
* camunda/camunda: /usr/local/camunda/lib

For example, to export to Datadog, download the io.micrometer:micrometer-registry-datadog JAR and place it in the ./lib folder of the distribution.

Running from the root of the distribution, you can use Maven to do this for you:

```bash
mvn dependency:copy -Dartifact=io.micrometer:micrometer-registry-datadog:1.14.4 -Dtransitive=false -DoutputDirectory=./lib
```

## Note
The version must be the same as the Micrometer version used by Camunda.

* Find this information by checking the distribution artifact on [Maven Central](https://central.sonatype.com/artifact/io.camunda/camunda-zeebe/dependencies).
* Select the distribution version you are using, and filter for micrometer to get the expected Micrometer version.

--- 
## ✅ Expected Outcome

By the end of this challenge, you’ll have:
- A running **Camunda Platform 8** setup emitting metrics through Micrometer.
- A working **StatsD + Datadog integration** to visualize those metrics.
- A deeper understanding of how observability is achieved in distributed workflow systems.
![statsD-result.png](images/statsD-result.png)
---
### 📚 Resources
* [**StatsD Spring Boot Configuration**](https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.export.statsd)
* [**Camunda Metrics & Monitoring**](https://docs.camunda.io/docs/self-managed/operational-guides/monitoring/metrics/#use-a-different-monitoring-system)
* [**Camunda Zeebe Maven Dependencies**](https://central.sonatype.com/artifact/io.camunda/camunda-zeebe/dependencies)