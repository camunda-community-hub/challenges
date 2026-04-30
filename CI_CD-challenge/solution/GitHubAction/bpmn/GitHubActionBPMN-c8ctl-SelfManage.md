# Camunda 8 BPMN Deployment CI/CD

This repo contains BPMN/DMN/Form assets and a GitHub Actions pipeline to deploy them to a Camunda 8 Self-Managed cluster using `c8ctl`.

It supports:

- **Reusable deployment workflow** (for dev/stage/prod)
- **Self-hosted runner** for local/dev deployments
- **OAuth2/M2M auth** against the Camunda 8 Orchestration REST API

---

## 0. Prerequisites

### 0.1. Local domain via `camunda-deployment-references`

For local/self-managed development, this setup assumes you are using the official reference deployment:

- Repo: [`camunda/camunda-deployment-references`](https://github.com/camunda/camunda-deployment-references)

Follow the relevant guide to:

1. Install Camunda 8 using the provided scripts.
2. Configure a **local domain** for your cluster (for example `camunda.example.com`) via Ingress + DNS/`/etc/hosts`.

Your `DEV_CAMUNDA_BASE_URL` secret should point to this domain, e.g.:
```bash
DEV_CAMUNDA_BASE_URL =  https://camunda.example.com
```

> Note: If you use a self-signed / local certificate (e.g. mkcert) you may need additional configuration in your runner; see the optional note in the workflow section below.
---

## 1. Workflows Overview

### 1.1. Reusable deployment workflow

File: `.github/workflows/GitHubActionBPMN-c8ctl-SelfManage.yaml`

- Trigger: `workflow_call` (never runs by itself)
- Job: `deploy-workflows`
- Runner: typically:
    - `runs-on: [self-hosted, dev]` for local/dev, or
    - `runs-on: ubuntu-latest` for shared/cloud runners
- Uses:
    - `actions/checkout` to get the repo
    - `actions/setup-node` (Node 22)
    - `@camunda8/cli` (`c8ctl`) for deployment
- Deploys everything under the configured `resource_paths` (default: `bpmn dmn forms`).

Interface (simplified):
```yaml
workflow_call:
  inputs:
    environment:
      type: string
      required: true
    project_dir:
      type: string
      required: false
      default: .
    resource_paths:
      type: string
      required: false
      default: "bpmn dmn forms"
  secrets:
    CAMUNDA_BASE_URL:
      required: true
    CAMUNDA_CLIENT_ID:
      required: true
    CAMUNDA_CLIENT_SECRET:
      required: true
    CAMUNDA_OAUTH_URL:
      required: true
    CAMUNDA_TOKEN_AUDIENCE:
      required: true

jobs:
  deploy-workflows:
    #    runs-on: ubuntu-latest
    runs-on: [self-hosted, dev]
    permissions:
      contents: read

    defaults:
      run:
        working-directory: ${{ inputs.project_dir }}

    env:
      ENVIRONMENT: ${{ inputs.environment }}
      RESOURCE_PATHS: ${{ inputs.resource_paths }}

      CAMUNDA_BASE_URL: ${{ secrets.CAMUNDA_BASE_URL }}
      CAMUNDA_AUTH_STRATEGY: OAUTH
      CAMUNDA_CLIENT_ID: ${{ secrets.CAMUNDA_CLIENT_ID }}
      CAMUNDA_CLIENT_SECRET: ${{ secrets.CAMUNDA_CLIENT_SECRET }}
      CAMUNDA_OAUTH_URL: ${{ secrets.CAMUNDA_OAUTH_URL }}
      CAMUNDA_TOKEN_AUDIENCE: ${{ secrets.CAMUNDA_TOKEN_AUDIENCE }}
```
### 1.2. Dev caller workflow

File: `.github/workflows/camunda-deploy-dev.yaml`

- Trigger:
    - `workflow_dispatch` (manual)
    - `push` to `dev` (if changes in BPMN/DMN/Forms or this workflow)
- Calls the reusable workflow:
```yaml
on:
  workflow_dispatch: {}              # manual trigger for testing
  push:
    branches:
      - dev
    paths:
      - "bpmn/**"
      - "dmn/**"
      - "forms/**"
      - ".github/workflows/camunda-deploy-dev.yaml"

jobs:
  deploy-dev:
    uses: ./.github/workflows/GitHubActionBPMN-c8ctl-SelfManage.yaml
    with:
      environment: dev
      project_dir: .
      resource_paths: "bpmn dmn forms"
    secrets:
      CAMUNDA_BASE_URL: ${{ secrets.DEV_CAMUNDA_BASE_URL }}
      CAMUNDA_CLIENT_ID: ${{ secrets.DEV_CAMUNDA_CLIENT_ID }}
      CAMUNDA_CLIENT_SECRET: ${{ secrets.DEV_CAMUNDA_CLIENT_SECRET }}
      CAMUNDA_OAUTH_URL: ${{ secrets.DEV_CAMUNDA_OAUTH_URL }}
      CAMUNDA_TOKEN_AUDIENCE: ${{ secrets.DEV_CAMUNDA_TOKEN_AUDIENCE }}
```
---

## 2. Self-Hosted Runner (dev)

For dev we typically run the deploy job on a **self-hosted runner** (your Mac) so it can reach your local/self-managed cluster.

### 2.1. Register the runner

In GitHub:

1. Repo → **Settings → Actions → Runners → New self-hosted runner**
2. Choose **macOS** and copy the commands.

On your Mac:
```bash
mkdir -p ~/actions-runner && cd ~/actions-runner
```
Download and extract runner (use URL from GitHub)
```bash
curl -o actions-runner-osx-x64.tar.gz -L <download_url>
tar xzf actions-runner-osx-x64.tar.gz
```
Configure with dev label
```bash
./config.sh 
  --url  https://github.com/jtruon/Camunda-8-Assets-Deployment 
  --token <RUNNER_TOKEN_FROM_GITHUB> 
  --labels dev
```
Start the runner
```bash
./run.sh
```


You should now see the runner **online** under  
**Settings → Actions → Runners** with labels: `self-hosted, dev, macOS, X64`.

---

## 3. Getting the API Key and Secret (clientId / clientSecret)
The `CAMUNDA_CLIENT_ID` and `CAMUNDA_CLIENT_SECRET` used by `c8ctl` are OAuth client credentials from Camunda 8, *not* from `c8ctl` itself.

For **Self-Managed** with Identity, there are two main steps:

### 3.1. Create an M2M application in Management Identity

1. Open **Management Identity** in your browser:
    - For a domain setup: `https://camunda.example.com/managementidentity`
    - For a local port-forward (from camunda-deployment-references docs): something like  
      `http://localhost:8085/managementidentity`
2. Log in as the **admin** user you configured in the Helm values (`identity.firstUser`).
3. Go to **Applications** and click **Add application**.
4. Choose **M2M** as the type.
5. Give it a name (e.g. `c8ctl-dev`).
6. After creation, open the application details:
    - Copy the **Client ID** → this will be your `CAMUNDA_CLIENT_ID`.
    - Copy the **Client Secret** → this will be your `CAMUNDA_CLIENT_SECRET`.

These are your “API key and secret” for CI/CD.
![SM-Github-c8ctl-Create-Secrets.png](images/SM-Github-c8ctl-Create-Secrets.png)
### 3.2. Grant API permissions for Orchestration

Still in **Management Identity**:

1. Open the M2M application you just created (`c8ctl-dev`).
2. Go to **Access to APIs** (or **Permissions**).
3. Assign permissions to the **Orchestration API** with at least:
    - `read:*`
    - `write:*`
4. Save.

This allows your M2M client to call the Orchestration REST API.

### 3.3. Link the client to a role in Orchestration Identity

Now switch to **Orchestration Identity**:

1. Open **Orchestration Identity**:
    - Domain setup: `https://camunda.example.com/identity`
    - Local port-forward setup: something like `http://localhost:8080/identity`
2. Log in as the same admin user.
3. Go to **Roles**.
4. Either:
    - Use an existing role (e.g. **Admin**), or
    - Create a new role with the desired Orchestration permissions.
5. Open the role → **Clients** tab → click **Assign client**.
6. Enter the **Client ID** from step 3.1 (e.g. `c8ctl-dev`) and assign it.
This links the OAuth client to the Orchestration permissions so tokens from that client can actually deploy BPMN, etc.
![SM-Github-c8ctl-Clients.png](images/SM-Github-c8ctl-Clients.png)
You now have:

- `clientId` → **Client ID** from Management Identity
- `clientSecret` → **Client Secret** from Management Identity
- Correct permissions wired in both Management Identity and Orchestration Identity

Use those in your GitHub `DEV_CAMUNDA_CLIENT_ID` and `DEV_CAMUNDA_CLIENT_SECRET` secrets.

---

## 4. Required GitHub Secrets (dev)

Set these in:  
**Repo → Settings → Secrets and variables → Actions**
![SM-Github-c8ctl-Secrets-Variables.png](images/SM-Github-c8ctl-Secrets-Variables.png)
For dev:

- `DEV_CAMUNDA_BASE_URL`  
  e.g. `https://camunda.example.com`
- `DEV_CAMUNDA_CLIENT_ID`  
  M2M client ID from Management Identity (e.g. `c8ctl-dev`)
- `DEV_CAMUNDA_CLIENT_SECRET`  
  Client secret for that M2M app
- `DEV_CAMUNDA_OAUTH_URL`  
  e.g. `https://camunda.example.com/auth/realms/camunda-platform/protocol/openid-connect/token`
- `DEV_CAMUNDA_TOKEN_AUDIENCE`  
  Usually `orchestration-api` for self-managed

The caller workflow maps these to the secrets required by the reusable workflow.

---

## 5. Running the Dev Deployment

### 5.1. Via manual trigger

1. Ensure your self-hosted runner is running (`./run.sh`).
2. In GitHub: **Actions → Camunda Deploy Dev**
3. Click **Run workflow**, choose branch (e.g. `dev`), and run.
You should see:
![SM-Github-c8ctl-Create-Local-Job-Runner.png](images/SM-Github-c8ctl-Create-Local-Job-Runner.png)
- Job `deploy-dev` using `runs-on: self-hosted, dev` (if configured that way)
- Logs in your local runner terminal (`./run.sh`)
- `c8ctl deploy bpmn dmn forms` output in the action logs

### 5.2. On push to `dev`

With this in `camunda-deploy-dev.yaml`:
```yaml
on:
  workflow_dispatch: {}              # manual trigger for testing
  push:
    branches:
      - dev
    paths:
      - "bpmn/**"
      - "dmn/**"
      - "forms/**"
      - ".github/workflows/camunda-deploy-dev.yaml"
```
---
![SM-Github-c8ctl-Result.png](images/SM-Github-c8ctl-Result.png)
## 6. Extending to Stage / Prod
   To add another environment (e.g. stage):

### 6.1 Create new secrets:
- STAGE_CAMUNDA_BASE_URL
- STAGE_CAMUNDA_CLIENT_ID
- STAGE_CAMUNDA_CLIENT_SECRET
- STAGE_CAMUNDA_OAUTH_URL
- STAGE_CAMUNDA_TOKEN_AUDIENCE
### 6.2 Create a caller workflow
Create a caller workflow, e.g. .github/workflows/camunda-deploy-stage.yaml:
```yaml
name: Camunda Deploy Stage

on:
  workflow_dispatch: {}

jobs:
  deploy-stage:
    uses: ./.github/workflows/GitHubActionBPMN-c8ctl-SelfManage.yaml
    with:
      environment: stage
      project_dir: .
      resource_paths: "bpmn dmn forms"
    secrets:
      CAMUNDA_BASE_URL: ${{ secrets.STAGE_CAMUNDA_BASE_URL }}
      CAMUNDA_CLIENT_ID: ${{ secrets.STAGE_CAMUNDA_CLIENT_ID }}
      CAMUNDA_CLIENT_SECRET: ${{ secrets.STAGE_CAMUNDA_CLIENT_SECRET }}
      CAMUNDA_OAUTH_URL: ${{ secrets.STAGE_CAMUNDA_OAUTH_URL }}
      CAMUNDA_TOKEN_AUDIENCE: ${{ secrets.STAGE_CAMUNDA_TOKEN_AUDIENCE }}
```

For stage/prod clusters that are publicly reachable with a real TLS certificate, you can run on `ubuntu-latest` and do not need any local TLS tweaks.

---

## 7. Troubleshooting
### Job stuck on “Waiting for a runner…”
- Check the runner is **Online** and has the `dev` label (if using `runs-on: [self-hosted, dev]`).
- Confirm the workflow actually uses those labels.
### `fetch failed` from `c8ctl`
- Typically network/auth, not BPMN:
    - Verify `CAMUNDA_BASE_URL` is reachable from the runner:
    - ```bash
      curl -kv "${CAMUNDA_BASE_URL}/v2/system/topology"
      ```
    - Double-check `DEV_CAMUNDA_CLIENT_ID` / `DEV_CAMUNDA_CLIENT_SECRET`:
      - They must match the M2M client created in Management Identity.
      - That client must have Orchestration API permissions and be linked to a role in Orchestration Identity.
### No “Camunda Deploy Dev” workflow in Actions sidebar
- The workflows list uses the **default branch** (usually `main`).
- Make sure `GitHubActionBPMN-c8ctl-SelfManage-dev.yaml` (and `GitHubActionBPMN-c8ctl-SelfManage.yaml`) are on the default branch (or change the default branch) if you want to see and run them from the UI.