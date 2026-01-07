# OIDC challenge
Connect a Camunda 8 to an OIDC (EntraID or PingFederate).

The cluster is an 8.7 (or an 8.8), multi tenant server.

Connect one user to access.

Check the [Solution/README.md](solution/README.md) for a step-by-step guide.


# Connect the OIDC server

Use / Create en OIDC server. Camunda already have an EntraId to test in the Azure environment.

In this OIDC, register or use an existing user.

# Create a multi tenant server

Create two tenants: tenant "San Francisco" and "Paris"

Allow user to connect to "San Francisco" only.
Deploy the process "ReviewCandidateGroup.bpmn" on this tenant. Create process instance: user can see tenants and task in Operate and TaskList?

Allow the user to connect to the tenant "Paris"
Deploy the process "processReview.bpmn" on this tenant.Create process instance: user can see tenants and tasks from both tenants in Operate and TaskList? 

Remove the user to the tenant "Paris". Verify that processes and tasks from this tenant are not accessible.

# Use role mapping

# Use a group in a candidate group
The OIDC contains a group "PostSale consulting", with the user1 inside.

Deploy the process "ReviewCandidateGroup.bpmn", and create a process instance.
User1 should see the task "ReviewPost", but not the task "ReviewQuality"



