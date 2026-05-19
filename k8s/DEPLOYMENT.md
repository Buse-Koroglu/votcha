# Votcha Platform - Kubernetes Deployment Runbook
This document serves as the official, step-by-step orchestration guide for deploying the Votcha platform components (Infrastructure, Database, Caching, Logging, Backend, Frontend, and Ingress routing) on a Kubernetes cluster.

---

## 1. Infrastructure Initialization & Security Setup

### Step 1.1: Namespace Creation
Initialize the dedicated and isolated `votcha-dev` environment along with global cluster configurations.
```bash
k apply -f 00-global/
```

### Step 1.2: GitHub Container Registry (GHCR) Secret Provisioning

Inject the Docker Registry credentials into the cluster to allow Kubernetes to securely pull backend and frontend images from GitHub Packages.

```bash
kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username=EnesUluc \
  --docker-password=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 \
  --docker-email=enesuluc04@gmail.com \
  -n votcha-dev
```

## 2. ELK Stack (Logging & Observability Core) Deployment

[CRITICAL] The backend service strictly depends on Elasticsearch connectivity at startup. The following steps must be fully operational before initializing the application layer.

### Step 2.1: Common Manifests & Elasticsearch Clusters

Apply shared logging configurations (ConfigMaps, RBAC) and spin up the primary Elasticsearch stateful cluster.

```bash
kubectl apply -f apps/votcha/logging/01-common/
kubectl apply -f apps/votcha/logging/02-elastic/
```

### Step 2.2: Health Gate & Sync Wait

Monitor the cluster and wait until the Elasticsearch pod reaches a healthy and stable 1/1 Running state. Do not proceed until this gate passes.
```bash
kubectl get pods -n votcha-dev -w
```

### Step 2.3: Kibana System Account Password Configuration

Securely update the internal kibana_system user password to enable proper handshake and data mapping authorization between Kibana and Elasticsearch.

```bash
kubectl exec -it elasticsearch-0 -n votcha-dev -- curl -s -X POST --cacert /usr/share/elasticsearch/config/certs/ca/ca.crt -u "elastic:enes.13" -H "Content-Type: application/json" https://localhost:9200/_security/user/kibana_system/_password -d '{"password":"enes.13"}'
```

### Step 2.4: TLS Certificate Extraction

Extract the internal CA and Kibana component certificates generated inside the Elasticsearch pod to the local relative path for application embedding.

```bash
kubectl exec -n votcha-dev elasticsearch-0 -- cat /usr/share/elasticsearch/config/certs/ca/ca.crt > ../certs/ca.crt

kubectl exec -n votcha-dev elasticsearch-0 -- cat /usr/share/elasticsearch/config/certs/kibana/kibana.crt > ../certs/kibana.crt

kubectl exec -n votcha-dev elasticsearch-0 -- cat /usr/share/elasticsearch/config/certs/kibana/kibana.key > ../certs/kibana.key
```

### Step 2.5: Kubernetes Native TLS Secret Generation

Package the locally extracted certificates into a generic Kubernetes Secret object named elk-certs so they can be mounted by the backend and Kibana components.

```bash
kubectl create secret generic elk-certs --from-file=../certs/ca.crt --from-file=../certs/kibana.crt --from-file=../certs/kibana.key -n votcha-dev
```

### Step 2.6: Kibana Interface Deployment

Deploy the Kibana administration dashboard and monitor its startup health.

```bash
kubectl apply -f apps/votcha/logging/03-kibana/
kubectl get pods -n votcha-dev -w
```

### Step 2.7: Log Collection Agents & Ingress Routing

Enable Logstash for log processing, Filebeat for node-level log shipping, and establish the specific ingress routing layers for the monitoring suite.

```bash
kubectl apply -f apps/votcha/logging/04-logstash/
kubectl apply -f apps/votcha/logging/05-filebeat/
kubectl apply -f apps/votcha/logging/06-ingress-rule/
```

## 3. Data & Caching Tier
### Step 3.1: PostgreSQL Database Initialization

Deploy the stateful relational database components including Persistent Volumes, PVCs, Deployments, and ClusterIP Services.

```bash
k apply -f data/postgres/
```

### Step 3.2: Redis Distributed Cache Deployment

Spin up the Redis StatefulSet caching infrastructure to provide rapid distributed synchronization capabilities across the platform.

```bash
k apply -f data/redis/
```

## 4. Microservices / Application Layer
### Step 4.1: Core API & Frontend UI Launch

Once the backing databases, caching layers, and elastic logging endpoints are fully healthy, initialize the application workload.

```bash
k apply -f apps/votcha/backend/
k apply -f apps/votcha/frontend/
```

## 5. Traffic Ingress & Public Edge Routing
### Step 5.1: Edge Controllers and Domain Mapping rules

Establish the primary Ingress Controller infrastructure and inject traffic routing manifests to map domain boundaries cleanly to front and back components.

```bash
k apply -f core/ingress/
k apply -f apps/votcha/ingress-rules/
```

## 6. Purge & Post-Tear-Down Maintenance (Optional)

[WARNING] The following actions will cause immediate and permanent data loss. Only execute these during complete environment refactoring or local disk cleanup routines.

```bash
sudo rm -rf /data/elasticsearch/*
sudo rm -rf /data/kibana/*
```