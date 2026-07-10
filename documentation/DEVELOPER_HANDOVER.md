# NHAIS GP Links Adaptor

## Description

The NHAIS GP Links Adaptor is a Spring Boot 3.5.6 healthcare integration microservice that bridges modern FHIR/JSON REST APIs with legacy EDIFACT EDI messaging standards. It enables General Practice (GP) surgeries to keep patient registration and demographic data synchronized with the Primary Care Registration Management (PCRM) system (formerly NHS NHAIS), operating as a stateful gateway through the NHS Message Exchange for Social Care and Health (MESH) service.

### Key Integration Partners

- **GP Systems**: Connect via FHIR REST API for patient registration and demographic updates
- **PCRM/NHAIS System**: Receives/sends EDIFACT-formatted HA/GP Links messages over MESH
- **Message Queue (ActiveMQ/Azure Service Bus)**: Inbound message delivery to GP systems and inter-service communication
- **MongoDB**: Maintains stateful conversation records, sequence numbers, and processing history

The adaptor manages four outbound transaction types (ACG, AMG, REG, DER) and eight inbound transaction types, maintaining strict sequence synchronization to prevent duplicate or out-of-order processing.

---

## Prerequisites

### Required Software

- Java Development Kit 21 (LTS) — Temurin recommended
- IntelliJ IDEA (Community or Ultimate)
- Lombok Plugin — Required for annotation processing
- Docker Desktop (or Docker Engine + Docker Compose) — For local development dependencies
- Git — Repository management
- Gradle (included via wrapper `./gradlew`)

### Required Services for Local Development

All services run in Docker containers managed by `docker-compose.yml`:

| Service | Purpose | Image | Port |
|---|---|---|---|
| MongoDB | State persistence (sequence numbers, transaction history) | `mongo` | 27017 |
| ActiveMQ | Message queue for inbound/outbound MESH messages and GP system messages | `rmohr/activemq` | 5672 (AMQP), 8161 (WebUI) |
| Fake MESH | Mock MESH API for local integration testing | `nhsdev/fake-mesh:0.2.0` | 8829 |

### Development Tools (Optional but Recommended)

- Studio 3T — MongoDB GUI for inspecting state documents
- Postman/Insomnia — REST API testing
- Mesh Bash Scripts — Located in `mesh` directory for MESH API debugging

---

## Specifications

### Compute & Memory Requirements

**Local Development (IDE)**
- CPU: 2+ cores recommended
- Memory: 4GB minimum for IntelliJ + containers; 8GB+ recommended for smooth development
- Disk: 5GB free space (Docker images: MongoDB 1GB, ActiveMQ 0.5GB, Fake MESH 0.3GB)

**Container Deployment (Single Instance)**
- CPU: 0.5–1 CPU
- Memory: 512MB minimum; 1GB recommended
- CPU Limits: 2 CPU cores (prevents resource exhaustion during polling cycles)
- Memory Limits: 2GB (accounting for JVM heap + off-heap structures)

**Scaled Deployment (Multiple Instances)**
- Single MongoDB replica set shared across all adaptor instances (no per-instance database needed)
- Single ActiveMQ broker shared across all instances (configured for multiple consumers)
- Distributed Lock: MongoDB-backed lock prevents duplicate MESH polling across instances

### Network Requirements

- Outbound HTTPS to MESH service (i.e. `msg.int.spine2.ncrs.nhs.uk`)
- MongoDB connectivity: Local (dev) or managed cloud instance (AWS DocumentDB, Azure Cosmos DB)
- ActiveMQ/Azure Service Bus: Internal or cloud-managed messaging
- Inbound REST API: Exposed on port 8080 (configurable via `NHAIS_OUTBOUND_SERVER_PORT`)

### Database

- MongoDB: >= 4.4 (or AWS DocumentDB equivalent)
- Collections: `outboundState`, `inboundState`, `outboundSequenceId`, `inboundSequenceId`, `meshMailBoxScheduler`
- TTL Indexes: Auto-expire documents after `NHAIS_MONGO_TTL` (default 30 days)

---

## Transaction Types Supported

### Outbound (GP → HA)

| Code | Name | Purpose |
|---|---|---|
| ACG | Acceptance | Patient acceptance/registration confirmation |
| AMG | Amendment | Demographic update (address, phone, etc.) |
| REG | Removal (Out of Area) | Patient moved out of registered area |
| DER | Deduction Request | Request HA to remove patient from system |

### Inbound (HA → GP)

| Code | Name | Purpose |
|---|---|---|
| APF | Approval | Patient registration approved |
| REF | Rejection (Wrong HA) | Patient belongs to different HA |
| AMF | Amendment | HA-initiated demographic update |
| DEF | Deduction | Patient deducted from GP patient list |
| FPN | FP69 Prior Notification | FP69 form expected (NHS England notifications) |
| FFR | FP69 Flag Removal | FP69 notification resolved |
| DRR | Deduction Request Rejection | Deduction request denied |
| CQN | Close Quarter Notification | Acknowledged but not forwarded to GP |

---

## External & Internal Linkages

### External Dependencies

- **PCRM/NHAIS**: HA system managing GP patient lists; communicates via EDIFACT over MESH
- **MESH API**: NHS Digital's secure message exchange; REST API with OAuth token authentication
- **FHIR R4 (HL7)**: GP system sends REST payloads conforming to FHIR Patient resource profile

### Internal Components

- **Spring Boot**: Health checks (`/health`)
- **Spring Data MongoDB**: Async state persistence with automatic TTL cleanup
- **Apache Qpid JMS**: AMQP client for ActiveMQ communication
- **HAPI FHIR**: Parsing and validation of FHIR R4 payloads

### Configuration Sources

- Environment variables
- See `README.md` in the repository for complete list of `NHAIS_*` configuration variables
- Key configs: `NHAIS_MESH_MAILBOX_ID`, `NHAIS_MESH_RECIPIENT_MAILBOX_ID_MAPPINGS`, `NHAIS_MONGO_URI`

---

## Running the Adaptor Through the IDE

### Step 1: Import Project into IntelliJ

Clone the repository:

```bash
git clone https://github.com/NHSDigital/integration-adaptor-nhais.git
cd integration-adaptor-nhais
```

Open the project:

1. File → Open → Select `integration-adaptor-nhais` folder
2. Click "New Window" when prompted
3. IntelliJ will offer to import Gradle project; accept

Configure JDK and Project Structure:

1. Go to Project Structure (⌘; on macOS, Ctrl+Alt+Shift+S on Linux/Windows)
2. SDKs → Add SDK → Select Java 21 installation
3. Project → Set Project SDK to Java 21
4. Modules → Ensure Module SDK is Java 21

### Step 2: Install Lombok Plugin

1. Go to Preferences → Plugins
2. Search for "Lombok"
3. Click "Install" for the JetBrains-developed Lombok plugin
4. Restart IntelliJ

### Step 3: Configure Environment Variables

Download or use the example env file:

```bash
cp nhais-env-example.yaml nhais-env-local.yaml
```

Install IntelliJ EnvFile plugin:

- Preferences → Plugins → Search "EnvFile" → Install

Create IntelliJ run configuration:

1. Run → Edit Configurations
2. Click + → Select "Spring Boot"
3. Name: "NHAIS Adaptor"
4. Main class: `uk.nhs.digital.nhsconnect.nhais.IntegrationAdaptorNhaisApplication`
5. EnvFile tab → Enable EnvFile → Add `nhais-env-local.yaml`

### Step 4: Start Dependencies

Open terminal in workspace root and start Docker containers:

```bash
docker-compose up mongodb activemq fake-mesh
```

Wait for output indicating services are ready:

- MongoDB: `"waiting for connections"`
- ActiveMQ: `"Apache ActiveMQ <version> (<host>) started"`
- Fake MESH: `"Listening on :8829"`

### Step 5: Run the Application

1. In IntelliJ, go to Run → Run 'NHAIS Adaptor'
2. Wait for startup message: `"Tomcat started on port 8080"`
3. Verify health:

```bash
curl http://localhost:8080/healthcheck
```

Expected output:

```json
{"status":"UP"}
```

### Debugging Tips

- **Breakpoints**: Set breakpoints in `FhirToEdifactService` or `EdifactToFhirService` to trace message flows
- **View MongoDB State**: Use Studio 3T to inspect `outboundState` collection as transactions are processed
- **Monitor Queues**: Open ActiveMQ WebUI at `http://localhost:8161/` (Username: `admin` / Password: `admin`)
- **MESH Messages**: Check Fake MESH mailbox using script in `mesh/mesh.sh`

---

## Running the Adaptor via Containers

### Single Instance

Build the Docker image:

```bash
export BUILD_TAG=latest
docker-compose build nhais
```

Start all services:

```bash
docker-compose up nhais mongodb activemq fake-mesh
```

Verify the adaptor is running:

```bash
curl http://localhost:8080/healthcheck
```

### Multiple Instances Behind Load Balancer

To test load distribution or HA failover:

```bash
export BUILD_TAG=latest
docker-compose build nhais
docker-compose -f docker-compose.yml -f docker-compose.lb.override.yml up --scale nhais=3 nhais mongodb activemq fake-mesh
```

This starts:

- 3× Adaptor instances
- 1× NGINX load balancer on port 8080 (round-robin routing)
- MongoDB, ActiveMQ, Fake MESH

To change scale while running:

```bash
docker-compose -f docker-compose.yml -f docker-compose.lb.override.yml up --scale nhais=2 --no-recreate nhais
docker-compose -f docker-compose.yml -f docker-compose.lb.override.yml restart nginx-lb  # Reload config
```

### Container Configuration

Environment variables are set in `docker-compose.yml`. Key variables are:

- `NHAIS_MESH_CERT_VALIDATION=false` — Disable TLS verification for fake-mesh
- `NHAIS_LOGGING_LEVEL=INFO` — Change to `DEBUG` for verbose logging
- `NHAIS_MONGO_URI=mongodb://mongodb:27017` — Service discovery via hostname

---

## How To Run Tests

### Prerequisites for All Test Types

Ensure Java 21 and Gradle are available:

```bash
java -version
./gradlew --version
```

### Unit Tests

Run fast, in-memory tests in parallel with build cache enabled:

```bash
./gradlew test
```

- Excludes `@Tag("component")` tests
- No external services required

**IDE Shortcut (IntelliJ)**: Navigate to `src` path → Right-click test folder → Run Tests

### Component Tests

Component tests test individual Spring beans in isolation with `@SpringBootTest(webEnvironment = MOCK)`:

```bash
./gradlew componentTest
```

- Includes only `@Tag("component")` tests
- Spins up embedded Spring context
- No ActiveMQ/MongoDB required

**Why separate from unit tests?** Spring dependency injection overhead; keep fast unit tests separate.

### Integration Tests

Full stack testing with real containers (MongoDB, ActiveMQ, Fake MESH via Testcontainers):

```bash
./gradlew integrationTest
```

- Spins up Docker-in-Docker (Testcontainers)
- Tests real MESH polling, queue consumption, EDIFACT parsing
- Uses test data in `src/intTest/resources/outbound_uat_data/` and `src/intTest/resources/inbound_uat_data/`
- Takes a while to run, especially if images need to be pulled first

**IDE Shortcut (IntelliJ)**: Navigate to `src` path from the project root → Right-click `/intTest` folder → Run Tests

**Troubleshooting Testcontainers:**

If you get: `"Can not connect to Ryuk at localhost:32779"` on your local workstation:

```bash
TESTCONTAINERS_RYUK_DISABLED=true ./gradlew integrationTest
```

> ⚠️ **Important**: ONLY disable Ryuk in local development and clean containers manually; CI/CD must have Ryuk enabled for proper container cleanup.

### All Tests

Run full test suite as the CI/CD pipeline would:

```bash
./gradlew check
```

This:

- Runs: checkstyle + unit tests + component tests + integration tests
- Generates JaCoCo code coverage reports in `build/reports/jacoco/`
- Will take a while to run, slower on first run (subsequent runs use build cache)

### Test Report Locations

| Test Type | Report Path |
|---|---|
| Unit | `build/test-results/test/` / `build/reports/tests/test/` |
| Component | `build/test-results/componentTest/` / `build/reports/tests/componentTest/` |
| Integration | `build/test-results/integrationTest/` / `build/reports/tests/integrationTest/` |
| Coverage | `build/reports/jacoco/test/html/index.html` |

Open HTML reports in a browser for detailed test results.

---

## Using Static Analysis Tools

### Checkstyle (Code Formatting/Style)

Enforces code style defined in `config/checkstyle/checkstyle.xml`:

```bash
./gradlew checkstyle
```

This runs on:

- main source set: `./gradlew checkStyleMain`
- test source set: `./gradlew checkstyleTest`
- intTest source set: `./gradlew checkstyleIntTest`

**Report**: `build/reports/checkstyle/` (HTML report)

**Fix locally**: Most violations auto-fixable in IntelliJ:

- Analyze → Run Inspection by Name → Search "Checkstyle"
- Run inspections and auto-fix

### Dependabot (Dependency Vulnerability Scanning)

GitHub Actions automatically scans dependencies weekly via `.github/dependabot.yml`:

- Maven/Gradle dependencies: Checks for known CVEs
- GitHub Actions: Checks for action updates

**Workflow:**

1. Dependabot detects vulnerable dependency (e.g., log4j 2.14.0 with RCE vulnerability)
2. Creates automated PR with update to 2.14.1 (or latest safe version)
3. CI/CD runs on PR; if tests pass, developer merges
4. If tests fail, developer investigates breaking changes and updates code

### SonarQube (Optional)

For detailed code quality analysis, SonarQube can be run locally (not part of standard CI/CD):

```bash
./gradlew sonarqube \
  -Dsonar.projectKey=nhais-adaptor \
  -Dsonar.sources=src/main \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<your-sonar-token>
```

Requires SonarQube server running locally. Configuration in `sonar-project.properties`.

### PITest (Mutation Testing)

Verify test quality by mutating code and checking if tests catch the mutations:

```bash
./gradlew pitest
```

This:

- Modifies code (e.g., changes `==` to `!=`)
- Runs tests; if tests fail, mutation was caught ("killed"); if pass, test is weak
- Generates report in `build/reports/pitest/`

Use for identifying gaps in test coverage quality (not just coverage percentage).

**Note**: Mutation testing is slow (~5–10 minutes); typically run before major refactors and be aware of false positives in complex logic.

---

## Caveats

### Critical Constraints & Known Limitations

#### 1. Stateful Conversation with PCRM

**DO NOT** ask to delete or reset MongoDB in production without coordinating manually with PCRM operators.

Sequence numbers in the `outboundSequenceId` and `inboundSequenceId` collections are critical for EDIFACT message synchronization. If sequence numbers are lost:

- PCRM rejects all future messages as "out of sequence"
- System cannot recover without manual HA intervention to reset their sequence counters
- Patient registration may be corrupted or duplicated

**Mitigation:**

- Enable MongoDB backups (AWS auto-backups, or manual snapshots every 24 hours)
- Monitor sequence ID collections for anomalies
- Never redeploy database to production without verified backup procedure

#### 2. MongoDB TTL Expiration is Non-Recoverable

Default `NHAIS_MONGO_TTL=P30D` automatically removes `outboundState` and `inboundState` documents after 30 days.

If a retransmission is needed after 30 days, the original state is gone; the system re-processes as a new transaction, potentially creating duplicates in PCRM.

**Mitigation:**

- Increase `NHAIS_MONGO_TTL` to match your retention/audit requirements (e.g., `P90D`)
- If required, archive old documents to separate MongoDB collection before TTL expiration

#### 3. MESH Polling Lock Timeout

Only one adaptor instance polls MESH at a time via MongoDB distributed lock (`meshMailBoxScheduler`).

**Issue**: If adaptor instance crashes during a polling cycle, the lock may persist:

- Other adaptor instances wait for the lock, but it's held by a dead instance
- Lock expires after `NHAIS_MESH_POLLING_CYCLE_DURATION_IN_SECONDS` (default 285 seconds = 4.75 min)
- During lock wait, no inbound messages are retrieved from MESH

**Mitigation:**

- Ensure adaptor instances have graceful shutdown hooks (Spring does this automatically)
- Monitor lock duration via MongoDB: `db.meshMailBoxScheduler.findOne()` should show recent timestamps
- If lock is stuck and has not expired, manually delete the lock document (only in emergencies)

#### 4. No E2E Testing Against Real PCRM

The OpenTest environment (NHS's integration testing service) is no longer available.

Cannot test against real PCRM system without production deployment.

**Current Solution**: fake-mesh (mock MESH API) provides synthetic EDIFACT messages for integration testing. This:

- Tests message parsing and conversion
- Tests state management and idempotency
- Does NOT test PCRM's actual business logic rules (e.g., duplicate patient checks, demographic validation)
- Does NOT test real MESH infrastructure (SSL, connectivity, quota enforcement)

**Workaround:**

- Carefully review EDIFACT spec (Appendix J of GP Links Specification) before deployment
- Use fake-mesh for pre-deployment validation, then coordinate with NHS for production smoke test

---

## Debugging

### Key Debugging Techniques

#### 1. MongoDB State Inspection

View in-flight transaction state using Studio 3T or MongoDB CLI:

```bash
# CLI: Connect to local MongoDB
mongo mongodb://localhost:27017/nhais

# View outbound transactions
db.outboundState.find().pretty()

# View inbound transactions
db.inboundState.find().pretty()

# Check sequence numbers
db.outboundSequenceId.find().pretty()

# View MESH polling lock status
db.meshMailBoxScheduler.findOne()
```

Key fields in `outboundState`:

- `_id`: MongoDB ObjectId
- `operationId`: Synthesized ID (format: `{tradingPartnerId}-{transactionNumber}`)
- `edifactSequenceNumber`: EDIFACT UNB segment number
- `transactionType`: ACG, AMG, DER, REG
- `createdTimestamp`, `processingTimestamp`: Timing info
- `lastSyncTimestamp`: When RECEP was received

#### 2. ActiveMQ Queue Inspection

View queues and stuck messages via WebUI:

```
http://localhost:8161/
Username: admin
Password: admin
```

Then:

1. Click "Manage ActiveMQ broker"
2. Click "Queues" tab
3. Select queue: `nhais_mesh_outbound`, `nhais_mesh_inbound`, `nhais_gp_system_inbound`
4. View message details: Click message ID to see header/payload

Troubleshoot stuck messages:

```bash
# Purge queue (use with caution; removes all messages)
# Option 1: Via WebUI (Purge button next to queue name)
# Option 2: Via shell
# docker exec -it activemq bash
# /opt/activemq/bin/activemq query --objname org.apache.activemq:type=Broker,brokerName=localhost -Q
```

#### 3. Fake MESH Message Inspection

Check sent/received messages in fake-mesh mailbox:

```bash
# Using mesh.sh script in mesh/ directory
./mesh.sh read-inbox  # List inbound messages in gp_mailbox
./mesh.sh read-messages <msg_id>  # View specific message content
```

#### 4. Application Logging

Adjust logging levels for verbose output:

**In IDE (via EnvFile):**

```
NHAIS_LOGGING_LEVEL=DEBUG
```

**In Docker:**

```bash
# Edit docker-compose.yml environment section
NHAIS_LOGGING_LEVEL=DEBUG

docker-compose up nhais
```

Key log messages to look for:

```
# Successful outbound transaction
[INFO] FhirToEdifactService - Converting FHIR resource to EDIFACT for transaction ACG-TES5-XX11

# Queue consumption
[INFO] OutboundQueueService - Consumed message from queue; sending to MESH

# MESH polling
[INFO] MeshMailBoxScheduler - Acquired polling lock; starting MESH poll cycle

# Inbound conversion
[INFO] EdifactToFhirService - Parsed EDIFACT APF message; publishing to GP queue

# Errors
[ERROR] OutboundQueueService - Failed to send EDIFACT message: SSL handshake failed
[ERROR] EdifactToFhirService - Invalid EDIFACT segment; skipping message
```

#### 5. Breakpoints in IDE

Set conditional breakpoints in key classes:

- `FhirController.registerPatient()` — Trace REST API entry
- `FhirToEdifactService.convert()` — Trace FHIR→EDIFACT conversion
- `OutboundQueueService.publishMessage()` — Trace queue handoff
- `MeshService.sendMessage()` — Trace MESH send
- `EdifactToFhirService.parse()` — Trace EDIFACT parsing
- `RegistrationConsumerService.handleMessage()` — Trace inbound routing

**Example:**

```
1. Set breakpoint at FhirToEdifactService.convert()
2. Make REST API call: POST /nhais/patients with FHIR payload
3. Debugger pauses at breakpoint
4. Inspect: fhirPatient, edifactMessage variables
5. Step through conversion logic
```

#### 6. MESH Bash Debugging

Use `mesh/mesh.sh` for manual MESH operations:

```bash
cd mesh

# Read (poll) inbox without acknowledging
./mesh.sh read-inbox

# Send a test message
./mesh.sh send-message <recipient_id> <file_path>

# Get message count
./mesh.sh count-messages

# View MESH config
cat env.fake-mesh.sh  # See MESH credentials and host
```

#### 7. Docker Logs

Inspect container logs for crashes or service issues:

```bash
# View adaptor logs
docker-compose logs nhais

# View MongoDB logs
docker-compose logs mongodb

# View ActiveMQ logs
docker-compose logs activemq

# Follow logs in real-time
docker-compose logs -f nhais

# View logs from a stopped container
docker-compose logs nhais --tail 50  # Last 50 lines
```

#### 8. Common Failure Patterns

| Error | Cause | Fix |
|---|---|---|
| MESH SSL handshake failed | `NHAIS_MESH_CERT_VALIDATION=true` for fake-mesh | Set to `false` in local dev |
| Cannot connect to MongoDB | MongoDB container not running or wrong URI | `docker-compose up mongodb` |
| ActiveMQ: Connection refused | ActiveMQ not running or wrong broker URL | `docker-compose up activemq` |
| EDIFACT segment UNE count mismatch | Invalid FHIR payload or conversion bug | Check test data; add DEBUG logging |
| Duplicate key error in MongoDB | Idempotency key collision or TTL bug | Analyze message ID generation |
| RECEP message not processed | Sequence number mismatch | Verify outbound transaction was sent first |

---

## CI/CD Pipeline Using GitHub Actions

### Pipeline Stages

#### Stage 1: PR Opened/Updated or Push to develop

**Workflow file**: `.github/workflows/build.yml`

**Trigger:**

- Pull Request to `develop` (opened, synchronized, reopened)
- Merge to `develop` branch

**Execution:**

1. `test.yml` workflow called (reusable)
    - Spins up Ubuntu container (`ubuntu-latest`)
    - Checks out code
    - Sets up Java 21 (Temurin)
    - Runs 4 parallel test jobs (with dependencies):
        - `checkstyle` runs first (gate-keeper)
        - `unit-tests`, `component-tests`, `integration_tests` wait for checkstyle
    - Integration tests spin up Docker-in-Docker (mongo, activemq, fake-mesh)
2. If all tests pass, continue to:
    - `generate-build-id` — Creates unique tag:
        - PR: `PR-{run_number}-{sha_short}` e.g., `PR-456-a1b2c3d`
        - develop merge: `develop-{run_number}-{sha_short}` e.g., `develop-456-a1b2c3d`
3. `publish.yml` workflow called (reusable)
    - AWS credential assumption via OIDC
    - Builds Docker image using `Dockerfile`
    - Logs into AWS ECR
    - Tags image: `{aws_account}.dkr.ecr.{region}.amazonaws.com/nhais:{build_id}`
    - Pushes to ECR
4. If PR, post comment with Build ID — provides image tag to be provided when deploying to PTL

**Pipeline Status:**

- All tests pass → Image published to ECR
- Checkstyle fails → Units/components/integration tests skip
- Tests fail → Build stops; PR marked "checks failed"

#### Stage 2: Release (Manual GitHub Release)

**Workflow file**: `.github/workflows/release.yml`

**Trigger**: GitHub Release published (manual action by maintainer)

**Execution:**

- Delegates to reusable workflow from NHS Digital's `integration-adaptor-actions` repo
- Pulls Docker image built from `develop` branch
- Retags as: `nia-nhais-adaptor:{release_tag}` (e.g., `nia-nhais-adaptor:1.4.0`)
- Pushes to DockerHub (public registry)
- Attaches release artifacts (JAR, SBOM, etc.)

**Access Control**: Requires `DOCKER_USERNAME` and `DOCKER_PASSWORD` secrets (managed by NHS Digital)

#### Stage 3: Dependabot Security Checks (Weekly)

**Configuration file**: `.github/dependabot.yml`

**Trigger**: Weekly scan (Sunday 2 AM UTC by default)

**Execution:**

- Scans `build.gradle` for Maven/Gradle dependencies
- Checks each dependency against known CVE databases
- If vulnerability found:
    - Creates PR: "Bump {package} from {old_version} to {new_version}"
    - PR title includes: "Security: CVE-XXXX"
    - CI/CD runs automatically on PR (`test.yml`)
    - If tests pass: Developers review and merge
    - If tests fail: Developers fix breaking changes or request Dependabot release new version

### Accessing Build Artifacts

**GitHub UI:**

1. Go to PR → "Checks" tab
2. Click `build.yml` job → "Summary"
3. Download artifacts:
    - Test reports (checkstyle, unit, integration)
    - Docker logs (integration test containers)

**Docker Image Access:**

- PR image: `{ecr}.dkr.ecr.{region}.amazonaws.com/nhais:PR-123-abc123`
- Requires AWS CLI credentials configured

Example:

```bash
aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin {account}.dkr.ecr.eu-west-1.amazonaws.com
docker pull {account}.dkr.ecr.eu-west-1.amazonaws.com/nhais:PR-123-abc123
```

### Pre-Commit Local Validation

Before pushing, developers should run locally to catch issues early:

```bash
# Run checkstyle
./gradlew checkstyleIntTest
./gradlew checkstyleMain
./gradlew checkstyleTest

# Run all tests
./gradlew check

# If all pass, push to PR branch
git push origin feature/my-branch
```

---

## Further Reading

For questions or issues:

- Check `AGENTS.md` for architectural patterns
- Review `README.md` for configuration reference
- Inspect test data in `src/intTest/resources/` for EDIFACT examples
- Run `./gradlew integrationTest` to verify local setup
- Consult NHS Digital's Guide to NHAIS/GP links documentation for EDIFACT spec