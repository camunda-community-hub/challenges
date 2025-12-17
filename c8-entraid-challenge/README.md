# EntraID challenge
Connect a Camunda 8 to an Entra ID.
The cluster is 8.7 (or an 8.8), multi tenant server.
Connect one user to access

# Connect the Entra ID server

Create en Entra ID server, or use an existing EntraId. Camunda already have an EntraId to test in the Azure environment.

In this EntraId, register or use an existing user.

# Create a multi tenant server

Create two tenants: tenant "San Francisco" and "Paris"

Allow user to connect to "San Francisco" only.
Deploy the process "processReview.bpmn" on this tenant. Create process instance: user can see tenants and task in Operate and TaskList?

Allow the user to connect to the tenant "Paris"
Deploy the process "processReview.bpmn" on this tenant.Create process isntance: user can see tenants and tasks from both tenants in Operate and TaskList? 

Remove the user to the tenant "Paris". Verify that process and tasks from this tenants is not not accessible.

# Use role mapping

# Use a group in a candidate group
The Entra ID contains a group "PostSale consulting", with the user1 inside.

Deploy the process "yyyyy", and create a process instance.
User1 should see the task.



