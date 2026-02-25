# Remote Java Debugger Challenge

This challenge is about connecting a remote Java debugger to a Camunda 8 cluster and validating the full debugging flow end-to-end.

## Challenge steps

1. Start a Camunda 8 cluster.
2. Start a remote debugging session and attach your debugger to the running cluster.
3. Add a breakpoint in an authentication method (for example, `getCamundaAuthentication()` in `DefaultCamundaAuthenticationProvider`).
4. Validate that after connecting and triggering authentication, the breakpoint pauses execution.

## Expected outcome

Attach your IDE debugger to the cluster and confirm that execution pauses on your breakpoint during authentication.

## Solution

See the implementation guide in [solution/README.md](solution/README.md).
