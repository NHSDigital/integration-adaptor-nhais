# GP Links - NHAIS Adaptor

NHAIS is a system that allows General Practice (GP) Surgeries to keep their patient registration and demographics data 
in sync with the regional Health Authorities (HA). Since the creation of this service the regional or area health 
authorities (approx 80) have since been replaced by a fewer number of successor organisations. There is however still a 
notion of every GP Practice's patient being registered with one of the HAs.

See the [Resources](#resources) section for links to the underlying services and standards.

## Adaptor Scope

The main objective of  the GP Links - NHAIS  Adaptor is to hide complex legacy standards and instead present a simple 
and consistent interface aligned to current NHSD national standards. The adaptor removes the requirement for a GP System 
to handle the complexities of EDIFACT and MESH messaging. To successfully integrate with NHAIS using this adaptor a GP 
System Supplier **MUST** have a complete understanding of the "HA/GP links registration GP systems specification" except 
where it directly involves EDIFACT. The specification contains many requirements pertaining to the GP System itself 
which are out of scope for the adaptor.

The patient registration and demographics portion of NHAIS is called HA/GP Links. NHAIS supports some features in 
addition to GP Links, but these are out of scope for the GP Links - NHAIS Adaptor project.

HA/GP Links messaging comprises several types of "transactions" used to update and reconcile patient lists and 
patient demographic data. The GP Links - NHAIS Adaptor supports the following transaction types:

Outbound (GP -> HA)

| Abbreviation | Description 
|--------------|-------------
| ACG          | Acceptance transaction  
| AMG          | Amendment transaction  
| REG          | Removal (Out of Area) transaction  
| DER          | Deduction Request transaction  

Inbound (HA -> GP)

| Abbreviation | Description 
|--------------|-------------
| AMF          | Amendment transaction  
| DEF          | Deduction transaction  
| APF          | Approval transaction  
| REF          | Rejection (Wrong HA) transaction  
| FPN          | FP69 Prior Notification transaction – Section 3.21  
| FFR          | FP69 Flag Removal transaction  
| DRR          | Deduction Request Rejection transaction  
| CQN*         | Close Quarter Notification (chapter 3.20, Chapter 3 page 154) (may be considered optional)

\* The adaptor acknowledges Close Quarter Notifications but does not forward them to the GP System.

## Workflows

Chapter 3 of the GP Links Specification describes each transaction type including workflow and processing diagrams. In 
this document "OUT-GOING" is the same is Outbound (GP -> HA) and  "IN-COMING" is the same as Inbound (HA -> GP). 
Transaction names and field names are consistent between the GP Links specification and the adaptor's documentation.

## Adaptor API

### Outbound (GP -> HA)

The GP System will send outbound messages using a HL7 FHIR R4 REST API: [Outbound (GP -> HA) OpenAPI Specification](specification/nhais-adaptor.yaml)

### Inbound (HA -> GP)

The GP System will receive inbound messages from an AMQP message queue. See [INBOUND.md](./INBOUND.md) for the 
documentation of the message formats.

### Examples

Examples of:

- outbound requests to the adaptor's API
- inbound replies (published to the Inbound Supplier MQ)
- inbound unsolicited messages (published to the Inbound Supplier MQ)

are provided as part of the adaptor's User Acceptance Tests.

Examples with filenames containing `app-j-` are copied from the _GP SYSTEMS SPECIFICATION - APPENDIX J - SAMPLE 
REGISTRATION EDIFACT MESSAGES_. Examples with filenames containing `live-` are sanitised copies of recent NHAIS live 
service transactions. Further synthetic examples round out the test coverage.

#### Outbound Examples

The [outbound_uat_data](src/intTest/resources/outbound_uat_data) folder contains examples of outbound (GP->HA) 
transactions. There is a sub-folder for each transaction type. Within each of those folder are sets of 2-3 files for 
each example:

* `<example-id>.fhir.json`: The JSON payload sent from the GP System to the Adaptor.
* `<example-id>.edifact.dat`: The EDIFACT file sent from the adaptor to the NHAIS instance for the request
* `<example-id>.notes.txt`: (If provided) a textual description of the transaction

#### Inbound Examples

The [inbound_uat_data](src/intTest/resources/inbound_uat_data) folder contains examples of inbound 
(HA->GP) transactions. There is a sub-folder for each transaction type. Within each of those folder are sets of 2-3 
files for each example:
                       
* `<example-id>.fhir.json`: The JSON message published into the Inbound Supplier MQ.
* `<example-id>.edifact.dat`: The EDIFACT file from an NHAIS instance to the adaptor
* `<example-id>.txt`: (If provided) a textual description of the transaction
* `<example-id>.recep.dat`: The RECEP file sent back to NHAIS in receipt of the inbound transaction. RECEP is not a GP 
system concern.

## Adaptor Architecture

[NHS Digital Developer Hub - NHAIS GP Links adaptor](https://digital.nhs.uk/developer/api-catalogue/nhais-gp-links/nhais-gp-links-adaptor)

## Resources

**[Guide to NHAIS/GP links documentation](https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation)**

The "Guide to NHAIS/GP links documentation" describes how to use the “NHAIS developer document library” (see below) and 
provides updates and clarifications to the original documentation.

**[NHAIS developer document library](https://digital.nhs.uk/services/nhais/nhais-developer-document-library)**

When this page refers to chapters and sections they are within these documents comprising the “HA/GP links registration 
GP systems specification”.

Chapters 1-4 describe the requirements for the system including UI requirements for the GP System

_Appendix J_ and then _FHS Reg v1.4_ should be read to understand the EDIFACT messaging standard.

**[Message Exchange for Social Care and Health (MESH)](https://digital.nhs.uk/services/message-exchange-for-social-care-and-health-mesh)**

The adaptor transmits EDIFACT HA/GP Links transactions over MESH. The adaptor uses the MESH REST API.

## Configuration

The adaptor reads its configuration from environment variables. The following sections describe the environment variables
 used to configure the adaptor. 
 
Variables without a default value and not marked optional are *MUST* be defined for the adaptor to run.

### General Configuration Options

| Environment Variable               | Default                   | Description 
| -----------------------------------|---------------------------|-------------
| NHAIS_OUTBOUND_SERVER_PORT         | 80                        | The port on which the outbound FHIR REST API and management endpoints will run
| NHAIS_LOGGING_LEVEL                | INFO                      | Application logging level. One of: DEBUG, INFO, WARN, ERROR. The level DEBUG **MUST NOT** be used when handling live patient data.

### Message Queue Configuration Options

| Environment Variable               | Default                   | Description 
| -----------------------------------|---------------------------|-------------
| NHAIS_AMQP_BROKERS                 | amqp://localhost:5672     | A comma-separated list of URLs to AMQP brokers (*)
| NHAIS_MESH_OUTBOUND_QUEUE_NAME     | nhais_mesh_outbound       | The name of the outbound (to MESH) message queue
| NHAIS_MESH_INBOUND_QUEUE_NAME      | nhais_mesh_inbound        | The name of the inbound (from MESH) message queue
| NHAIS_GP_SYSTEM_INBOUND_QUEUE_NAME | nhais_gp_system_inbound   | The name of the inbound (to GP System) message queue
| NHAIS_AMQP_USERNAME                |                           | (Optional) username for the AMQP server
| NHAIS_AMQP_PASSWORD                |                           | (Optional) password for the AMQP server
| NHAIS_AMQP_MAX_REDELIVERIES        | 3                         | The number of times an message will be retried to be delivered to consumer. After exhausting all retires, it will be put on DLQ.<queue_name> dead letter queue

(*) Active/Standby: The first broker in the list always used unless there is an error, in which case the other URLs will be used. At least one URL is required.

### Mongodb Configuration Options

The adaptor configuration for mongodb can be configured two ways: using a connection string or providing individual 
properties. This is to accommodate differences in the capabilities of deployment automation frameworks and varying 
environments.

Option 1: If `NHAIS_MONGO_HOST` is defined then the adaptor forms a connection string from the following properties:

| Environment Variable             | Default | Description 
| ---------------------------------|---------|-------------
| NHAIS_MONGO_DATABASE_NAME        | nhais   | Database name for Mongo
| NHAIS_MONGO_HOST                 |         | Mongodb host
| NHAIS_MONGO_PORT                 |         | Mongodb port
| NHAIS_MONGO_USERNAME             |         | (Optional) Mongodb username. If set then password must also be set.
| NHAIS_MONGO_PASSWORD             |         | (Optional) Mongodb password
| NHAIS_MONGO_OPTIONS              |         | (Optional) Mongodb URL encoded parameters for the connection string without a leading ?
| NHAIS_MONGO_TTL                  | P30D    | (Optional) Time-to-live value for inbound and outbound state collection documents as an [ISO 8601 Duration](https://en.wikipedia.org/wiki/ISO_8601#Durations)
| NHAIS_COSMOS_DB_ENABLED          | false   | (Optional) If true the adaptor will enable features and workarounds to support Azure Cosmos DB

Option 2: If `NHAIS_MONGO_HOST` is undefined then the adaptor uses the connection string provided:

| Environment Variable             | Default                   | Description 
| ---------------------------------|---------------------------|-------------
| NHAIS_MONGO_DATABASE_NAME        | nhais                     | Database name for Mongo
| NHAIS_MONGO_URI                  | mongodb://localhost:27017 | Mongodb connection string

**Trust Store Configuration**

These optional properties configure a trust store with private CA certificates. See 
[OPERATING.md - AWS DocumentDB TLS configuration](OPERATING.md#AWS DocumentDB TLS configuration) for more information.

| Environment Variable                | Default       | Description 
| ------------------------------------|---------------|-------------
| NHAIS_SSL_TRUST_STORE_URL           |               | (Optional) URL of the trust store JKS. The only scheme currently supported is `s3://`
| NHAIS_SSL_TRUST_STORE_PASSWORD      |               | (Optional) Password used to access the trust store

## MESH API

### MESH API Connection Configuration

Configure the MESH API connection using the following environment variables:

| Environment Variable             | Default                   | Description 
| ---------------------------------|---------------------------|-------------
| NHAIS_MESH_MAILBOX_ID            |                           | The mailbox id used by the adaptor to send and receive messages. This is the sender of outbound messages and the mailbox where inbound messages are received.
| NHAIS_MESH_MAILBOX_PASSWORD      |                           | The password for NHAIS_MESH_MAILBOX_ID
| NHAIS_MESH_SHARED_KEY            |                           | A shared key used to generate auth token and provided by MESH operator (OpenTest, PTL, etc)
| NHAIS_MESH_HOST                  |                           | The **Complete URL** with trailing slash of the MESH service. For example: https://msg.int.spine2.ncrs.nhs.uk/messageexchange/
| NHAIS_MESH_CERT_VALIDATION       | true                      | "false" to disable certificate validation of SSL connections
| NHAIS_MESH_ENDPOINT_CERT         |                           | The content of the PEM-formatted client endpoint certificate
| NHAIS_MESH_ENDPOINT_PRIVATE_KEY  |                           | The content of the PEM-formatted client private key
| NHAIS_MESH_SUB_CA                |                           | The content of the PEM-formatted certificate of the issuing Sub CA. Empty if NHAIS_MESH_CERT_VALIDATION is false
| NHAIS_MESH_RECIPIENT_MAILBOX_ID_MAPPINGS |                   | (1) The mapping between each recipient HA Trading Partner Code (HA Link Code) to its corresponding MESH Mailbox ID mapping. There is one mapping per line and an equals sign (=) separates the code and mailbox id. For example: "COD1=A6840385\nHA01=A0047392"
| NHAIS_SCHEDULER_ENABLED          | true                      | Enables/disables automatic MESH message downloads

(1) The three-character "Destination HA Cipher" required for each outbound API request uniquely identifies that patient's 
managing organisation. Each managing organisation also has a four-character "HA Trading Partner Code" (HA Link Code) uniquely
identifying that patient's managing organisation for the purpose of EDIFACT messaging. Finally, each "HA Trading Partner Code"
is assigned a MESH Mailbox ID: the mailbox to which the EDIFACT files for a given recipient are sent. The mappings between
organisations' "HA Trading Partner Codes" and their MESH Mailbox IDs are controlled by this variable. Note: A "Destination HA Cipher" 
can usually be converted into a "HA Link Code" by appending 1 or 01 to create the four-character code. If in doubt consult 
with the operator of the NHAIS instance for the correct value.

The following three variables control how often the adaptor performs a MESH polling cycle. During a polling cycle the 
adaptor will download and acknowledge up to "the first 500 messages" (a MESH API limit).

Important: If the MESH mailbox uses workflows other than `NHAIS_REG` and `NHAIS_RECEP` then these messages must be
downloaded and acknowledged by some other means in a timely manner. The adaptor will skip messages with other workflow
ids leaving them in the inbox. If more than 500 "other" messages accumulate the adaptor wil no longer receive new 
inbound GP Links messages.

| Environment Variable                                 | Default | Description 
| -----------------------------------------------------|---------|-------------
| NHAIS_MESH_CLIENT_WAKEUP_INTERVAL_IN_MILLISECONDS    | 60000   | The time period (in milliseconds) between when each adaptor instance "wakes up" and attempts to obtain the lock to start a polling cycle
| NHAIS_MESH_POLLING_CYCLE_MINIMUM_INTERVAL_IN_SECONDS | 300     | The minimum time period (in seconds) between MESH polling cycles
| NHAIS_MESH_POLLING_CYCLE_DURATION_IN_SECONDS         | 285     | The duration (in seconds) fo the MESH polling cycle

The MESH API specifies that a MESH mailbox should be checked "a maximum of once every five minutes". The variable 
`NHAIS_MESH_POLLING_CYCLE_MINIMUM_INTERVAL_IN_SECONDS` controls how often the adaptor will check its mailbox for new 
messages. This should not be set to less than 300 seconds. A time lock in the database prevents the polling cycle from
running more often than this minimum interval. Each adaptor instance will wake up every 
`NHAIS_MESH_CLIENT_WAKEUP_INTERVAL_IN_MILLISECONDS` to try this time lock. Therefore, the maximum polling cycle interval
is the sum of these two values.

Only one instance of the adaptor runs the polling cycle at any given time to prevent duplicate processing. The value
`NHAIS_MESH_POLLING_CYCLE_DURATION_IN_SECONDS` prevents one polling cycle from overrunning into the next time interval.
This value must always be less than `NHAIS_MESH_POLLING_CYCLE_MINIMUM_INTERVAL_IN_SECONDS`.

## Operating

Refer to [OPERATING.md](OPERATING.md) for tip about how to operate the adaptor in the production environment.

## Development

The following sections provide the necessary information to develop the GP Links - NHAIS adaptor.

The adaptor configuration has sensible defaults for local development. Some overrides might be required where the 
"secure by default" principle takes precedence:

* `NHAIS_MESH_CERT_VALIDATION: "false"` - if using fake-mesh then certificate validation must be disabled
* `NHAIS_LOGGING_LEVEL: "DEBUG"` - consider using DEBUG logging while developing

An easy way to override the default configuration is to use an [EnvFile](https://plugins.jetbrains.com/plugin/7861-envfile).
Example: [nhais-env-example.yaml](./nhais-env-example.yaml)

### Pre-requisites (IntelliJ)

* Install a Java JDK 11. [AdoptOpenJdk](https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot) is recommended.
* Install [IntelliJ](https://www.jetbrains.com/idea/)
* Install the [Lombok plugin](https://plugins.jetbrains.com/plugin/6317-lombok)
* Install [Docker](https://www.docker.com/products/docker-desktop)

### Import the integration-adaptor-nhais project

* Clone this repository
* Open the cloned `integration-adaptor-nhais` folder
* Click pop-up that appears: (import gradle daemon)
* Verify the project structure


    Project structure -> SDKs -> add new SDK -> select adoptopenjdk-11.jdk/Contents/Home  (or alternative location)
                      -> Project SDK -> Java 11 (11.0.7)
                      -> Module SDK -> Java 11 (11.0.7)

### Start Dependencies

* [mongo](https://hub.docker.com/_/mongo/): MongoDB Docker images
* [rmohr/activemq](https://hub.docker.com/r/rmohr/activemq): ActiveMQ Docker images
* [nhsdev/fake-mesh](https://hub.docker.com/r/nhsdev/fake-mesh): fake-mesh (mock MESH API server) Docker images

Run `docker-compose up mongodb activemq fake-mesh`

### Running

**From IntelliJ***

Navigate to: IntegrationAdaptorNhaisApplication -> right click -> Run

**Inside a container**

    export BUILD_TAG=latest
    docker-compose build nhais
    docker-compose up nhais
    
**Inside multiple containers, behind a load balancer**

Docker Compose allows running multiple instances behind a nginx load balancer in using round robin routing.

    export BUILD_TAG=latest
    docker-compose build nhais
    docker-compose -f docker-compose.yml -f docker-compose.lb.override.yml up --scale nhais=3 nhais

This command will start three instances of the adaptor behind a load balancer on port 8080

To change the scale number while all services are running run the same "up" command with new scale value and then 
restart the load balancer container (so it will become aware of instance count change).

### Running Tests

**All Tests**

    ./gradlew check

**Unit Tests**

    ./gradlew test
    
**Component Tests**

`@Tag("component")` annotates all component tests and the `gradle test` task will not run them. To run component tests 
you have to use command:

    ./gradlew componentTest

**Integration Tests**

A separate source folder [src/intTest](./src/intTest) contains integration tests. To run the integration tests use:

    ./gradlew integrationTest

**Non-functional Tests**

See [NFR_TESTING.md](./NFR_TESTING.md)

### Debugging

#### Adaptor components overview

**WARNING**: The specific components are out of date but how they relate to external dependencies is generally correct.

![Adaptor Component Diagram](/documentation/nhais_diagram_plus_key.jpeg)

#### Mongo DB

To view data in MongoDB:

* Download [Robo 3T](https://robomongo.org/)
* Open Robo 3T -> Create new connection with details as below:
  * Type: Direct Connection
  * Name: nhais
  * Address: localhost : 27017
* View adaptor collections by navigating to nhais -> collections -> (select any collection)

#### ActiveMQ

To view messages in the ActiveMQ Web Console:

* Open browser and navigate to: http://localhost:8161/
  * Username: admin
  * Password: admin
* Click manage ActiveMQ broker
* Click Queues tab
* Select desired queue
* Select a message ID to display information of message 

#### MESH API

A `mesh.sh` bash script exists for testing or debugging MESH. For more information see: [mesh/README.md](/mesh/README.md)

#### Fake MESH

A mock implementation of the MESH API is available for local development. The latest version is in Github at
[mattd-kainos/fake-mesh](https://github.com/jamespic/fake-mesh). _It is a fork of [jamespic/fake-mesh](https://github.com/jamespic/fake-mesh)._

The [nhsdev Docker Hub](https://hub.docker.com/repository/docker/nhsdev/fake-mesh) hosts released fake-mesh images.

### Common Issues

#### My test won't run

    Execution failed for task ':test'.
    > No tests found for given includes:
    
Check your imports and ensure you're using JUnit5 `org.junit.jupiter.api.*` classes instead of the older JUnit4 `org.junit.*` ones.

#### Application repeatedly throws exceptions

ActiveMQ has not been configured with dead-lettering. You must purge all invalid messages from the queues.

#### Integration Tests: "Can not connect to Ryuk at localhost:32779"

An optional component (Ryuk) of the [Testcontainers](https://www.testcontainers.org/) framework used for integration tests
fails to start on some developer workstations. It is possible to disable this component with an environment variable:

    TESTCONTAINERS_RYUK_DISABLED=true
    
Note: This variable must not be set when the tests run automatically as part of a pipeline. Ryuk guarantees container
cleanup up after each test (even if the test crashes) and disabling it could lead to a resource leak in the build 
environment.