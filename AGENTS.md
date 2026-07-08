# AI Agent Guide for GP Links Adaptor (NHAIS)

## Project Overview

This is a Spring Boot 3.5.6 healthcare integration adaptor that translates between FHIR/JSON REST APIs and EDIFACT EDI messages, acting as a gateway between GP systems and the health authority via MESH (Message Exchange for Social Care and Health).

**Key Constraint**: Changes must not break the stateful conversation with PCRM/NHAIS systems. MongoDB contains critical sequence numbers and state that synchronize GP and HA systems.

## Architecture: Three Message Pipelines

### 1. Outbound (GP → HA via MESH)

- **REST Entry**: `FhirController` (FHIR Parameters), `AmendmentController` (JSON Patch)
- **Conversion**: `FhirToEdifactService`, `JsonPatchToEdifactService` 
- **Stream**: REST → `OutboundQueueService.publishMessage()` → ActiveMQ → `OutboundQueueService.consumeMessage()` → `MeshService.sendMessage()` → MESH
- **State Tracking**: `OutboundState` MongoDB document tracks conversion timestamp, EDIFACT sequences, transaction type (ACG, AMG, REG, DER)
- **Critical**: Sequence numbers in `outboundSequenceId` collection must never be leaked/lost

### 2. Inbound (HA → GP via MESH)

- **MESH Poll**: `MeshService.readNextMessage()` runs on distributed lock via `MeshMailBoxScheduler` (MongoDB-backed, prevents duplicate reads)
- **Routing**: `InboundQueueService` listener → `RegistrationConsumerService` OR `RecepConsumerService`
- **Conversion**: `EdifactToFhirService` (approval/rejection/deduction) OR `EdifactToPatchService` (amendments)
- **GP Publishing**: Messages published to `NHAIS_GP_SYSTEM_INBOUND_QUEUE_NAME` with headers: `OperationId`, `TransactionType`
- **RECEP Handling**: Special acknowledgement messages update corresponding `OutboundState` document with receipt confirmation

### 3. State Management

- **Idempotency**: Indexed lookups on both `OutboundState` and `InboundState` prevent duplicate processing
- **TTL Expiration**: `NHAIS_MONGO_TTL` (default P30D) auto-removes old documents via MongoDB TTL index
- **Compound Keys**: Sequences tracked as `<type>-<sender>-<recipient>` (e.g., `TN-TES5-XX11`)
- **OperationId**: Synthesized from GP Trading Partner + transaction number for matching inbound approvals/rejections

## Critical Patterns & Conventions

### Transaction Types (3-char abbreviation codes)

**Outbound**: ACG (acceptance), AMG (amendment), REG (removal/out-of-area), DER (deduction request)  
**Inbound**: APF (approval), REF (rejection), AMF (amendment), DEF (deduction), FPN (FP69 prior notification), FFR (FP69 flag removal), DRR (deduction request rejection), CQN (close quarter notification - acknowledged but not forwarded)

**Pattern**: Transaction abbreviations are used as identifiers throughout in domain objects, database queries, and MESH workflow IDs.

### Configuration via Environment Variables

All configuration is environment-driven (no properties files). Critical ones:
- `NHAIS_MESH_RECIPIENT_MAILBOX_ID_MAPPINGS`: Multi-line mapping of HA codes to MESH mailbox IDs (parsed by custom property parser)
- `NHAIS_MONGO_URI` OR `NHAIS_MONGO_HOST` + `NHAIS_MONGO_PORT`: Choose one pattern
- `NHAIS_AMQP_BROKERS`: Comma-separated list (active/standby, not load-balanced)
- MESH credentials: `NHAIS_MESH_MAILBOX_ID`, `NHAIS_MESH_ENDPOINT_CERT` (PEM format)

### Testing Structure

- **Unit/Component Tests**: `@Tag("component")` excluded from `./gradlew test`, run with `./gradlew componentTest`
- **Integration Tests**: Separate source set `src/intTest`, use `@ExtendWith(SpringExtension.class)`, run with `./gradlew integrationTest`
- **Test Data**: Located in `src/intTest/resources/outbound_uat_data/` and `src/intTest/resources/inbound_uat_data/` with triplets: `.fhir.json`, `.edifact.dat`, `.notes.txt`
- **Testcontainers**: Used for integration tests; `TESTCONTAINERS_RYUK_DISABLED=true` env var needed on some workstations (disable only in dev, not CI)
- **No JUnit4**: Codebase uses JUnit5 (`org.junit.jupiter.api.*`); JUnit4 imports will fail
- **E2E Testing NOT AVAILABLE**: OpenTest environment is no longer available. E2E testing against real PCRM/NHAIS systems is not possible. Use local `fake-mesh` for integration testing only.

### Build Commands (Gradle)

```bash
./gradlew build                # Full build (unit, component, integration tests)
./gradlew test                 # Unit tests only (component tests excluded)
./gradlew componentTest        # Component tests only
./gradlew integrationTest      # Integration tests only
./gradlew bootJar              # Build application JAR
./gradlew checkstyle          # Code style validation
```

**CI/CD Pipeline**: Jenkins is no longer used. Build, test, and deployment are handled through GitHub Actions. Do NOT reference Jenkinsfiles as deployment mechanisms.

### GitHub Actions CI/CD Workflows

All CI/CD is orchestrated through GitHub Actions workflows in `.github/workflows/`:

#### 1. **build.yml** (Main Build Pipeline)
Triggered on: PR to `develop` or push to `develop`

**Workflow**:
1. Runs test workflow (checkstyle, unit tests, component tests, integration tests)
2. Generates unique Build ID using `scripts/create_build_id.sh` (format varies by branch: `PR-<run>-<sha>` or `develop-<run>-<sha>`)
3. Builds Docker image and publishes to AWS ECR with Build ID as tag
4. Comments on PR with Build ID for reference

**Key Outputs**: Build ID tag used for Docker image in ECR; comment added to pull requests with image reference

#### 2. **test.yml** (Reusable Test Workflow)
Called by build.yml; can be invoked independently

**Sequential Test Jobs**:
- **checkstyle**: Code style validation via `./gradlew checkStyleMain checkstyleTest checkstyleIntTest`
- **unit-tests** (depends on checkstyle): `./gradlew test --parallel --build-cache`
- **component-tests** (depends on checkstyle): `./gradlew componentTest --build-cache`
- **integration_tests** (depends on checkstyle): Spins up docker-compose (mongodb, activemq, fake-mesh), runs `./gradlew integrationTest`, captures docker logs

**Artifacts**: Test reports, checkstyle reports, and docker logs uploaded after each job

**Environment**: Java 21 (Temurin), Gradle with build cache enabled, Docker Compose for integration tests

#### 3. **publish.yml** (Reusable Docker Publish Workflow)
Called by build.yml after tests pass

**Steps**:
1. AWS credential assumption via OIDC (uses `AWS_ACCOUNT_ID`, `AWS_ROLE_TO_ASSUME`, `AWS_REGION` secrets)
2. Builds Docker image using provided build-id as tag
3. Logs into AWS ECR
4. Pushes image to `{AWS_ACCOUNT_ID}.dkr.ecr.{AWS_REGION}.amazonaws.com/nhais:{build-id}`
5. Logs out and cleans up credentials

**Permissions**: `id-token: write` for OIDC, `contents: read`

#### 4. **release.yml** (Release to DockerHub)
Triggered on: GitHub release published

**Process**: Delegates to reusable workflow from NHS Digital integration-adaptor-actions repo (`NHSDigital/integration-adaptor-actions/.github/workflows/release-adaptor-container-image.yml`)
- Publishes image to DockerHub as `nia-nhais-adaptor:{release_tag}`
- Attaches release artifacts

#### 5. **Dependabot Automation** (.github/dependabot.yml)
- **Gradle**: Weekly checks for dependency updates, creates automated PRs
- **GitHub Actions**: Weekly checks for action updates, creates automated PRs

### Docker & Development

- **Multi-stage build**: Gradle cache layer, then build layer (Dockerfile line 1-12)
- **Docker Compose**: `docker-compose up mongodb activemq fake-mesh` for local development
- **BUILD_TAG**: Required env var for image versioning; local dev uses `export BUILD_TAG=latest`
- **Fake MESH**: Mock MESH API at `nhsdev/fake-mesh:0.2.0`; development requires `NHAIS_MESH_CERT_VALIDATION=false`
- **Database persistence**: MongoDB should persist between runs in production; dev uses in-memory or ephemeral volumes

### Deployment Patterns

- **Stateful**: Adaptor must not be scaled without distributed locking (already implemented via MongoDB)
- **Message Broker**: ActiveMQ (prod), Azure Service Bus, or AmazonMQ supported; requires dead-letter queue configuration
- **AWS DocumentDB**: Supported with custom trust store; optionally auto-enable with `NHAIS_COSMOS_DB_ENABLED=true`
- **Health checks**: Application exposes Spring Boot Actuator (included in starter-actuator)

## Key Files & Their Responsibilities

| File/Directory | Purpose |
|---|---|
| `uk.nhs.digital.nhsconnect.nhais.outbound.*` | FHIR/JSON → EDIFACT conversion, REST endpoints |
| `uk.nhs.digital.nhsconnect.nhais.inbound.*` | EDIFACT → FHIR/JSONPatch conversion, queue publishing |
| `uk.nhs.digital.nhsconnect.nhais.mesh.*` | MESH polling, scheduling, HTTP communication |
| `uk.nhs.digital.nhsconnect.nhais.model.*` | Domain objects for EDIFACT segments, FHIR mappings |
| `uk.nhs.digital.nhsconnect.nhais.configuration.*` | Spring config, property binding |
| `src/intTest/resources/outbound_uat_data/` | Test fixtures with real/synthetic transaction examples |
| `INBOUND.md` | Authoritative spec for inbound message headers/formats |
| `README.md` | Configuration reference, local dev setup, troubleshooting |

## Common Pitfalls & Solutions

**Sequence Number Resets**: If MongoDB is deleted/cleared, the system loses sync with PCRM. Sequence state is not recoverable without manual HA intervention.

**Dead Letter Queues**: Undeliverable messages must be manually handled; no automatic recovery. Broker must be strictly configured with redelivery limits and DLQ.

**MESH Polling Lock**: Only one adaptor instance polls MESH at a time; the lock is held in MongoDB via `MeshMailBoxScheduler`. If lock expires/corrupts during a crash, manually clear the inbound state to recover.

**Amendment vs Acceptance**: Amendments use EDIFACT UNB segment reuse rules (specific sequences); failures here corrupt the conversation.

**RECEP Matching**: RECEP messages match outbound transactions via EDIFACT sequence numbers, not OperationId. Updates must be transactional in MongoDB.

## Deprecated & Unavailable (DO NOT USE)

- **Jenkins**: Deprecated. Do not reference Jenkinsfiles or propose Jenkins-based pipelines. Modern CI/CD systems are used for deployment.
- **OpenTest Environment**: No longer available. E2E testing against real PCRM/NHAIS systems is not possible. Integration tests must use local `fake-mesh` mock only.
- **OpenTest Deployment**: Do not propose deployment to OpenTest or reference OpenTest configuration files (e.g., `export-env-vars.opentest.example.sh`).

Respect these constraints when proposing solutions; if OpenTest is required for a solution, the task is not achievable with current infrastructure.

## Code Quality & Security Requirements

### Mandatory Pre-Commit Checks

All code changes MUST pass these checks before submission:

1. **SpotBugs**: Static analysis for potential bugs. Run with `./gradlew spotbugsMain`. Must have zero high-severity findings on modified code.
2. **Checkstyle**: Code style validation. Run with `./gradlew checkstyle`. All violations must be fixed.
3. **Dependabot**: Dependency vulnerability scanning. GitHub Actions automatically checks all dependencies. Address any flagged CVEs before merging.

### CI/CD Enforcement

- SpotBugs and Checkstyle run automatically in GitHub Actions during PR checks (part of test.yml)
- Dependabot creates automated PRs for vulnerable dependencies weekly
- PRs cannot merge if code quality or security checks fail

## When Modifying Code

1. **Check if state persists**: Does the change affect sequence numbers, state tracking, or EDIFACT segment generation? If yes, impacts idempotency.
2. **Run code quality checks**: Execute locally:
   - `./gradlew checkstyle` (must pass)
   - `./gradlew spotbugsMain` (must have no new high-severity findings)
   - Review any Dependabot alerts on dependencies you modify
3. **Run full test suite**: `./gradlew check` (not just unit tests)
4. **Test with integrationTest**: Real EDIFACT parsing, MESH communication paths need coverage
5. **Verify configuration**: New env vars must be documented in README.md and added to docker-compose.yml
6. **Preserve backward compatibility**: Old adaptor instances may coexist; breaking serialization of OutboundState/InboundState risks data corruption







