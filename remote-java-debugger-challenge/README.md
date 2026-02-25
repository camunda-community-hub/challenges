# Remote Java Debugger Challenge

This guide shows how to connect a Java debugger to Camunda 8 in Kubernetes.

Important context: [solution/camunda-values.yaml](solution/camunda-values.yaml) is prepared for Helm chart `13.4.2` on Azure Kubernetes Service, with OpenID Connect and HTTPS already configured.

For this chart version, use Camunda source tag `8.8.11`.

## Goal

Turn on Java debug mode for Orchestration and connect from your IDE.

## What is included

- Clean values file with placeholders: [solution/camunda-values.yaml](solution/camunda-values.yaml)
- Java debug mode in `orchestration.javaOpts`
- Debug port in `orchestration.service.extraPorts`

## 1) Fill placeholders

Edit [solution/camunda-values.yaml](solution/camunda-values.yaml) and replace all placeholder values (`<...>`) with your own values.

## 2) Confirm debug settings

The file already contains:

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
- `suspend=n` means startup does not wait for the debugger.
- Port `5005` is the Java debug port.

## 3) Deploy with your standard Helm flow

Apply [solution/camunda-values.yaml](solution/camunda-values.yaml) with your existing deployment process.

## 4) Open the debug port safely

First, check services in your namespace:

```bash
kubectl get services -n <NAMESPACE>
```

You should see something like this:

```text
camunda-platform-zeebe-gateway               ClusterIP   10.0.92.7     <none>        9600/TCP,8080/TCP,26500/TCP,5005/TCP    20m
```

Do not expose this port to the public.
Use `kubectl port-forward` from your machine:

```bash
kubectl port-forward svc/camunda-platform-zeebe-gateway 5005:5005 -n <NAMESPACE>
```

## 5) Set up Camunda source code

Before you connect the debugger, set up the Camunda source code:

```bash
git clone https://github.com/camunda/camunda.git
cd camunda
git fetch --tags
git tag -l "8.8.11*"
git checkout 8.8.11
```

After `git checkout 8.8.11`, a detached `HEAD` state is expected.

## 6) Compile Camunda source code

Compile the code before attaching the debugger (it will last for a while):

```bash
mvn clean install -DskipTests
```

## 7) Connect the debugger

### IntelliJ

1. Run → Edit Configurations → Add New → Remote JVM Debug
2. Debugger mode: `Attach to remote JVM`
3. Host: `localhost`
4. Port: `5005`
5. Use module classpath: `camunda-authentication` (example used for testing)
6. Start configuration

Reference screenshot: [solution/Remote JVM Debug.png](solution/Remote%20JVM%20Debug.png)

## 8) Verify it works

This is only an example for verification. You can use other classes and other Camunda components based on your debugging target.

1. Set a breakpoint at the start of the `getCamundaAuthentication()` method in `DefaultCamundaAuthenticationProvider.java`.
2. Open the browser and log in to Camunda.
3. The breakpoint should trigger during login and show the authentication process.

Reference screenshot: [solution/DefaultCamundaAuthenticationProvider breakpoint.png](solution/DefaultCamundaAuthenticationProvider%20breakpoint.png)
