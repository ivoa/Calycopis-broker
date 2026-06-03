# Multi-Broker Costs and Metrics Demonstration

This demonstration deploys three Calycopis Execution Broker instances, each configured with different cost and metric profiles, to show how users can compare offers and select the best platform for their workload.

## Architecture

```
┌─────────────────── Podman Network: calycopis-demo ───────────────────┐
│                                                                       │
│  ┌─ broker-alpha ──┐  ┌─ broker-beta ───┐  ┌─ broker-gamma ──┐      │
│  │ PostgreSQL      │  │ PostgreSQL      │  │ PostgreSQL      │      │
│  │ Broker :8082    │  │ Broker :8082    │  │ Broker :8082    │      │
│  │ (Green HPC)     │  │ (Cloud)         │  │ (Budget)        │      │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘      │
│                                                                       │
│  ┌─ demo-client ───────────────────────────────────────────────┐     │
│  │ Config container  │  Cursor CLI container (interactive)     │     │
│  └─────────────────────────────────────────────────────────────┘     │
└───────────────────────────────────────────────────────────────────────┘
```

### Broker Profiles

| Broker | Profile | Compute | IO (MB/s) | Monetary | Energy (kWh) | Carbon (gCO2) |
|--------|---------|---------|-----------|----------|--------------|---------------|
| Alpha  | Green HPC | 250-300 | 800-1200 | $0.30-0.80 | 0.01-0.03 | 2-8 |
| Beta   | Cloud | 120-160 | 200-400 | $0.05-0.15 | 0.05-0.15 | 15-40 |
| Gamma  | Budget | 60-90 | 50-100 | $0.02-0.08 | 0.10-0.30 | 30-80 |

## Prerequisites

- Podman
- `pwgen`, `yq`, `jq`, `curl`
- The broker container image (`calycopis/broker:latest`) — built during the setup below

## Quick Start

### 1. Build the broker image

From the project root (`Calycopis-broker/github-zrq`):

```bash
cd java
./mvnw clean package -DskipTests -Dcalycopis-platform=docker

cd ..
podman build \
    --build-arg jarfile=calycopis-broker-1.0.6-SNAPSHOT.jar \
    --tag calycopis/broker:latest \
    -f docker/java-runtime/Dockerfile \
    java/target/
```

### 2. Build the client image

```bash
cp /path/to/calycopis_schema_client-1.0.6-py3-none-any.whl demo/docker/demo-client/

podman build \
    --tag calycopis/demo-client:latest \
    -f demo/docker/demo-client/Dockerfile \
    demo/docker/demo-client/
```

### 3. Deploy

```bash
demo/bin/deploy.sh
```

This creates:
- The `calycopis-demo` Podman network
- Three broker pods (`broker-alpha`, `broker-beta`, `broker-gamma`), each with PostgreSQL and a broker container
- A `demo-client` pod
- Generated credentials in `demo/run/`

### 4. Configure user accounts

```bash
demo/bin/configure.sh
```

This creates a `demo-user` account on all three brokers.

### 5. Run the smoke test

```bash
demo/bin/smoke-test.sh
```

Verifies each broker returns offers with its configured cost/metric values.

### 6. Start the interactive client

```bash
podman run -it \
    --pod demo-client \
    --volume ./demo:/workspace:z \
    --env-file demo/run/demo-user.env \
    calycopis/demo-client:latest \
    /bin/bash
```

Inside the container, copy `USER-AGENT.md` as `AGENTS.md` and start Cursor:

```bash
cp /workspace/USER-AGENT.md /workspace/AGENTS.md
cursor /workspace
```

### 7. Teardown

```bash
demo/bin/teardown.sh
```

## Demo Scenario

1. **User**: "Run a Docker container that generates 1000 random numbers and computes statistics"

2. **Agent** creates an `ExecutionRequest` and submits to all three brokers

3. **Agent** presents a comparison table:

   | Attribute | Alpha (Green HPC) | Beta (Cloud) | Gamma (Budget) |
   |---|---|---|---|
   | Monetary cost | $0.30 - $0.80 | $0.05 - $0.15 | $0.02 - $0.08 |
   | Energy (kWh) | 0.01 - 0.03 | 0.05 - 0.15 | 0.10 - 0.30 |
   | Carbon (gCO2) | 2.0 - 8.0 | 15.0 - 40.0 | 30.0 - 80.0 |
   | Compute score | 250 - 300 | 120 - 160 | 60 - 90 |
   | IO throughput | 800 - 1200 | 200 - 400 | 50 - 100 |

4. **User** selects based on preference:
   - **Fastest**: Alpha (highest compute score)
   - **Cheapest**: Gamma (lowest monetary cost)
   - **Greenest**: Alpha (lowest carbon/energy)

5. **Agent** accepts the selected offer and monitors execution

6. **Agent** displays the final result with phase, messages, and connectors

## Directory Structure

```
demo/
├── README.md                     # This file
├── USER-AGENT.md                 # Cursor AI agent instructions
├── bin/
│   ├── deploy.sh                 # Deploy all pods
│   ├── teardown.sh               # Remove all pods
│   ├── configure.sh              # Create demo user accounts
│   └── smoke-test.sh             # Verify deployment
├── config/
│   ├── broker-alpha/
│   │   └── platform.yaml         # Green HPC profile
│   ├── broker-beta/
│   │   └── platform.yaml         # Cloud profile
│   └── broker-gamma/
│       └── platform.yaml         # Budget profile
├── docker/
│   └── demo-client/
│       └── Dockerfile            # Client container image
└── run/                          # Generated at runtime (gitignored)
    ├── demo-env.sh               # Broker URLs and admin credentials
    ├── demo-user.env             # Demo user credentials
    ├── alpha/                    # Generated config for broker-alpha
    ├── beta/                     # Generated config for broker-beta
    └── gamma/                    # Generated config for broker-gamma
```
