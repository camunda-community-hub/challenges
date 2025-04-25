# Deploy workers in a self manage cluster

This action have two steps:
* rebuild the image and save it in a package repository (for example, GitHub package)
* deploy the image on a cluster, to validate it

# Rebuild the image
Create a directory .github, and inside a directory workflows


![Directory workflow.png](images/GitHubAction.png)

Add the docker-build.yaml file

```yaml
name: worker-1-build-publish-image

on:
  push:
    branches: [ "main" ]  # or your branch name

jobs:
  build:
    runs-on: ubuntu-latest

    permissions:
      contents: read
      packages: write  # to publish to GHCR

    steps:
      - name: Checkout code
        uses: actions/checkout@v4


      - name: Log in to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Checkout code
        uses: actions/checkout@v4

      - name: Log in to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build Docker image
        run: |
          docker build -t ghcr.io/${{ github.repository }}:latest .

      - name: Push Docker image
        run: |
          docker push ghcr.io/${{ github.repository }}:latest
```

With this configuration, Docker recompile the project to build the image.

At the end, the image is present in the package
![img.png](images/GitHub-CheckPackagePublished.png)

# Deploy the image on a Kubernetes cluster

## Adapt the kubernetes deployment file

Retrieve the full image name of the worker. Go to the tab `packages` 

![Access package](images/GitHub-AccessPackages.png)

Access the package of your worker:
![Retrieve the image name](images/GitHub-GetImageName.png)

The information is located after the `docker pull` : the name is `ghcr.io/pierre-yves-monnet/c8-automate-test:latest`

In the file `deployment-worker.yaml`, reference this name on the `image` line:

```
          image: ghcr.io/pierre-yves-monnet/c8-automate-test:latest
          # must be set to always: then any kubectl apply will reload the latest image
          imagePullPolicy: Always
```

> Attention: the `imagePullPolicy` but be set to Always. The workflow will run a kubectl apply, and to be sure to create the last version, it must pull systematically the image  
 

## Collect the connection

### On Google
 
For Google Cloud, create a service account.

Access to IAM, and select `service account`

![img.png](images/Google-IAMServiceAccount.png)

Click on `Create service account`
![img.png](images/Google-ServiceAccount-name.png)

Click Create and continue

Give roles `Kubernetes Engine Developer` and `Kubernetes Engine Cluster Viewer`

![img.png](Google-ServiceAccount-Roles.png)

Click on Done.

On the list of all service account, find the new service, and click on it. On the first page, click on `Keys`

![img.png](images/Google-SerciceAccount-Keys.png)

Click on `Create New key` and select `Json`

![img.png](images/Google-ServiceAccount-AddJsonKey.png)

A key is generated, and downloaded on your machine.



On GitHub go to `Settings`, then `Secrets and variables` and 

![Access Secrets](images/GitHub-AccessSecrets.png)

Click on `New repository secret`, and add the variable `GCP_SA_KEY` and the value generated before.

![img_1.png](images/GitHub-Google-AddGCPKey.png)

Add a variable `GCP_REGION` and give the region of the cluster

![img.png](images/GithHub-Google-Region.png)

Add a variable `GCP_CLUSTERNAME` and give the name of the cluster

![img.png](images/GithHub-Google-ClusterName.png)
## Create a new workflow
Create this workflow in the repository (under .github/worklows). Name it `publish worker on cluster`

```yaml
name: Deploy In Cluster

on:
  workflow_run:
    workflows: ["worker-1-build-publish-image"]  # Name of the workflow to depend on
    types:
      - completed

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: install GKE
        run: |
          echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | sudo tee -a /etc/apt/sources.list.d/google-cloud-sdk.list
          curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key --keyring /usr/share/keyrings/cloud.google.gpg add -
          sudo apt update
          sudo apt-get install google-cloud-sdk-gke-gcloud-auth-plugin kubectl
          export USE_GKE_GCLOUD_AUTH_PLUGIN=True

      - name: Set up Google Cloud SDK
        uses: google-github-actions/auth@v2
        with:
          credentials_json: '${{ secrets.GCP_SA_KEY }}'

      - name: Set up gcloud CLI
        uses: google-github-actions/setup-gcloud@v2
        with:
          project_id: your-gcp-project-id

      - name: Get GKE credentials
        run: |
          gcloud container clusters get-credentials ${{ secrets.GCP_CLUSTERNAME }} --region ${{ secrets.GCP_REGION }}
      - name: Deploy to Kubernetes
        run: |
          kubectl apply -f deployment-worker.yaml -n camunda
```

Note: 
* This workflow depends on the first step, which build the worker.
* The GitHub action must log to your cluster. The image must install the gCloud CLI to do that.

