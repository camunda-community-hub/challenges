# Remote Java Debugger - Solution

This solution is currently focused on the OIDC scenario.

## 1) Configure the cluster to accept a debugger connection

Use this shared setup for the active OIDC scenario.

### Context

- OIDC values file: [camunda-values.yaml](oidc-connection/camunda-values.yaml)
- Prepared for Helm chart `13.4.2` on AKS
- Camunda source tag used in this example: `8.8.11`

### 1.1 Fill placeholders

Use the OIDC values file and replace all placeholder values (`<...>`) with your own values.

### 1.2 Confirm debug settings

The OIDC values file contains Java debug mode and debug service port:

```yaml
orchestration:
  javaOpts: >-
    ...
    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

  service:
    extraPorts:
      - name: debug
        protocol: TCP
        port: 5005
        targetPort: 5005
```

Notes:
- `suspend=n` means startup does not wait for debugger attach.
- `5005` is the Java debug port.

### 1.3 Deploy with your Helm flow

Apply the OIDC values file using your standard deployment process.

### 1.4 Open the debug port safely

Check services:

```bash
kubectl get services -n <NAMESPACE>
```

Expected example:

```text
camunda-platform-zeebe-gateway  ClusterIP  ...  9600/TCP,8080/TCP,26500/TCP,5005/TCP
```

Use local port-forward (recommended):

```bash
kubectl port-forward svc/camunda-platform-zeebe-gateway 5005:5005 -n <NAMESPACE>
```

### 1.5 Set up Camunda source

```bash
git clone https://github.com/camunda/camunda.git
cd camunda
git fetch --tags
git tag -l "8.8.11*"
git checkout 8.8.11
```

Detached `HEAD` is expected after checkout.

### 1.6 Compile source

```bash
mvn clean install -DskipTests
```

### 1.7 Connect debugger from IntelliJ

1. Run → Edit Configurations → Add New → Remote JVM Debug
2. Mode: `Attach to remote JVM`
3. Host: `localhost`
4. Port: `5005`
5. Use module classpath matching your target class (examples below use `camunda-authentication`)
6. Start configuration

Reference: [Remote JVM Debug.png](images/Remote%20JVM%20Debug.png)

## 2) Debug OIDC connection

OIDC is the first implemented scenario.

### Prerequisite

Complete section `1) Configure the cluster to accept a debugger connection` first.

Use the OIDC values file: [camunda-values.yaml](oidc-connection/camunda-values.yaml).

### Where to put the breakpoint

Place a breakpoint at the beginning of:

- `DefaultCamundaAuthenticationProvider#getCamundaAuthentication()`

This is a practical entry point to validate the OIDC authentication flow.

Reference image: [DefaultCamundaAuthenticationProvider breakpoint.png](images/DefaultCamundaAuthenticationProvider%20breakpoint.png)

### How to trigger

1. Open the Camunda web application in your browser.
2. Start login with your OIDC user.
3. Wait for the debugger to stop on the breakpoint.

### Validation

The scenario is successful when:

- IDE debugger is attached to `localhost:5005`
- Execution pauses on your breakpoint during authentication

## 3) Extend with new scenarios

Possible next scenarios:

- Debug default/basic connection
- Debug connector runtime/pod
- Debug process-instance creation flow

When adding a new scenario, use this structure:

1. **Prerequisite**: Reuse section `1) Configure the cluster to accept a debugger connection` in this file and specify the scenario values file.
2. **Where to put the breakpoint**: Define the class/method and why this breakpoint is relevant.
3. **How to trigger**: Explain the minimal actions to hit the breakpoint.
4. **Validation**: Explain what confirms the scenario is successful.
