# NHAIS Adaptor

NHAIS is a system that allows General Practice (GP) Surgeries to keep their patient registration and demographics data 
in sync with the regional Health Authorities (HA). Since the creation of this service the regional or area health 
authorities (approx 80) have since been replaced by a fewer number of successor organisations. There is however still a 
notion of every GP Practice’s patient being registered with one of the HAs.

See the [Resources](#resources) section for links to the underlying services and standards.

## Adaptor Scope

The patient registration and demographics portion of NHAIS is called HA/GP Links. NHAIS supports some features in 
addition to GP Links but these are out of scope for the NHAIS Adaptor project.

HA/GP Links messaging is comprised of several types "transactions" used to update and reconcile patient lists and 
patient demographic data. The following transaction types are supported by the NHAIS Adaptor:

GP Links to HA

| Abbreviation | Description 
|--------------|-------------
| ACG          | Acceptance transaction  
| AMG          | Amendment transaction  
| REG          | Removal (Out of Area) transaction  
| DER          | Deduction Request transaction  

HA to GP Links 

| Abbreviation | Description 
|--------------|-------------
| AMF          | Amendment transaction  
| DEF          | Deduction transaction  
| APF          | Approval transaction  
| REF          | Rejection (Wrong HA) transaction  
| FPN          | FP69 Prior Notification transaction – Section 3.21  
| FFR          | FP69 Flag Removal transaction  
| DRR          | Deduction Request Rejection transaction  
| *            | Close Quarter Notification (chapter 3.20, Chapter 3 page 154) (may be considered optional)

\* Close Quarter Notification is acknowledged by the adaptor but not forwarded to the GP System

The goal of the NHAIS Adaptor is to remove the requirement for a GP System to handle the complexities of EDIFACT and 
MESH messaging.

## Workflows

### Initiated by GP Practice

| Request (GP -> HA)     | Possible Replies (HA -> GP)      |
|--------------|------------------------|
| Acceptance   | Approval, Rejection (Wrong HA |
| Amendment    | None, Amendment        |
| Removal      | TBD                    |
| Deduction    | Deduction, Deduction Request Rejection |

### Initiated by HA

TODO

## Adaptor API

### Outbound (GP -> HA)

The GP System will send outbound messages using a HL7 FHIR R4 REST API: [Outbound (GP -> HA) OpenAPI Specification](specification/nhais-adaptor.yaml)

### Inbound (HA -> GP)

The GP System will receive inbound messages from an AMQP message queue. The messages will be HL7 FHIR R4.

## Adaptor Architecture

TODO

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

The EDIFACT HA/GP Links transactions are transmitted over MESH. The adaptor will use the MESH REST API.

## Development

The following sections are intended to provide the necessary info on how to configure and run the NHAIS adaptor.

Environment Variables are used throughout application, an example can be found in `nhais-env-example.yaml`. 

### Pre-requisites

* Install an Java JDK 11. AdoptOpenJdk is recommended: https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot
* Download Lombok plugin : https://plugins.jetbrains.com/plugin/6317-lombok
* MongoDB: `docker-compose up mongodb`
* RabbitMQ: `docker-compose up activemq`

### Developer setup:

Open integration-adaptor-nhais -> click pop-up that appears (import gradle daemon)

Check the following:

Project structure   -> SDKs -> add new SDK -> select adoptopenjdk-11.jdk/Contents/Home
                    -> Project SDK -> java 11 (11.0.7)
                    -> Module SDK -> java 11 (11.0.7)
                    
To run in dev env, navigate to: IntegrationAdaptorNhaisApplication, right click, run() main.

## Getting started 

Debug database and quese for NHAIS:

[NHAIS Diagram with key](/documentation/nhais_diagram_plus_key.jpeg)

### Mongo DB

To view data in MongoDB:

Download Robo 3T
https://robomongo.org/

Open Robo 3T -> Create new connection with details as below:

- Type: Direct Connection
- Name: nhais
- Address: localhost : 27017

View NHAIS by navigating to nhais -> collections -> (select any table)

### ActiveMQ

To view messages in ActiveMQ Queue:

Open browser and native to: http://localhost:8161/

- Username: admin
- Password: admin

Click manage ActiveMQ broker

Click Queues tab

Select desired queue

Select a message ID to display information of message 

## Configuration

The service is configured using environment variables. Variables without a default value and not marked optional are *required* to be provided
when the service is run.

| Environment Variable               | Default                   | Description 
| -----------------------------------|---------------------------|-------------
| NHAIS_OUTBOUND_SERVER_PORT         | 80                        | The port on which the outbound FHIR REST API will run
| NHAIS_AMQP_BROKERS                 | amqp://localhost:5672     | A comma-separated list of URLs to AMQP brokers for the outbound (to mesh) message queue (*)
| NHAIS_MESH_OUTBOUND_QUEUE_NAME     | nhais_mesh_outbound       | The name of the outbound (to mesh) message queue
| NHAIS_MESH_INBOUND_QUEUE_NAME      | nhais_mesh_inbound        | The name of the inbound (from mesh) message queue
| NHAIS_GP_SYSTEM_INBOUND_QUEUE_NAME | nahis_gp_system_inbound   | The name of the inbound (to gp system) message queue
| NHAIS_AMQP_USERNAME                |                           | (Optional) username for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_PASSWORD                |                           | (Optional) password for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_MAX_RETRIES             | 3                         | The number of times a request to the outbound (to mesh) broker(s) will be retried
| NHAIS_AMQP_RETRY_DELAY             | 100                       | Milliseconds delay between retries to the outbound (to mesh) broker(s)

(*) Active/Standby: The first broker in the list always used unless there is an error, in which case the other URLs will be used. At least one URL is required.

### Mongodb Configuration Options

The adaptor configuration for mongodb can be configured two ways: using a connection string or providing individual 
properties. This is to accommodate differences in the capabilities of deployment automation frameworks and varying 
environments.

If any value is set for `NHAIS_MONGO_HOST` then the following properties will be used to create a connection string:

| Environment Variable             | Default | Description 
| ---------------------------------|---------|-------------
| NHAIS_MONGO_DATABASE_NAME        | nhais   | Database name for Mongo
| NHAIS_MONGO_HOST                 |         | Mongodb host
| NHAIS_MONGO_PORT                 |         | Mongodb port
| NHAIS_MONGO_USERNAME             |         | (Optional) Mongodb username. If set then password must also be set.
| NHAIS_MONGO_PASSWORD             |         | (Optional) Mongodb password
| NHAIS_MONGO_OPTIONS              |         | (Optional) Mongodb URL encoded parameters for the connection string without a leading ?

If no value is set for `NHAIS_MONGO_HOST` then the following properties are used:

| Environment Variable             | Default                   | Description 
| ---------------------------------|---------------------------|-------------
| NHAIS_MONGO_DATABASE_NAME        | nhais                     | Database name for Mongo
| NHAIS_MONGO_URI                  | mongodb://localhost:27017 | Mongodb connection string

## Configuring your AMQP Broker

* Your broker must be configured with a limited number of retries and deadletter queues
* Your broker must use persistent queues to avoid loss of data

### Using AmazonMQ

A persistent broker (not in-memory) must be used to avoid data loss.

A configuration profile that includes settings for retry and deadletter must be applied: https://activemq.apache.org/message-redelivery-and-dlq-handling.html

Amazon gives their Amazon MQ endpoint with the scheme `amqp+ssl://` but this is not supported / recognised by Java.

You will need to change the scheme to `amqps://`

### Using Azure Service Bus

Your Azure Service Bus must use MaxDeliveryCount and dead-lettering: https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-dead-letter-queues#exceeding-maxdeliverycount

### Using ActiveMQ in a local container / docker-compose (for development)

Admin UI is at http://localhost:8161/
Login is admin/admin

**Note**: this broker will not have limited retries or deadletter enabled. Purge the queue using the web console to clear any errors.

## AWS Document DB

In the "Connectivity & security" tab of the cluster a URI is provided to "Connect to this cluster with an application".
Replace <username>:<insertYourPasswordHere> with the actual mongo username and password to be used by the application.
The value of `NHAIS_MONGO_URI` should be set to this value. Since the URI string contains credentials we recommend that
the entire value be managed as a secured secret.

The user must have the `readWrite` role or a custom role with specific privileges.

## MESH API

TODO: NHAIS Adaptor MESH configuration when MESH integration is implemented

For local test scripts see [mesh/README.md](/mesh/README.md)

### OpenTest

TODO

### Fake MESH

Clone https://github.com/mattd-kainos/fake-mesh from our fork

Follow the instructions in that repository's README.md to run it (using Docker is recommended)

## Running

* Set and export environment variables defined in `nhais-env-example.yaml`
* NOTE: Enfile does not appear to work in IntelliJ / Grade. You will need to set each required variable 
  (see application.yml) in the run configuration if you use these.
* Run `uk.nhs.digital.nhsconnect.nhais.IntegrationAdaptorNhaisApplication`

### Running with Docker Compose

    docker-compose build
    docker-compose up

### Running Tests

Ensure development dependencies are installed before running the tests

    ./gradlew check

#### Unit Tests

    ./gradlew test
    
#### Component Tests

All component tests are annotated with "component" Tag.
Component tests will not be launched using gradle test task.
To run component tests you have to use command:

    ./gradlew componentTest

### Integration Tests

Integration tests are located in a separate source folder src/intTest.
To run the integration tests use:

    ./gradlew integrationTest

## Management endpoints:

Healthcheck:

    curl localhost:8080/healthcheck

Metrics:

    curl localhost:8080/metrics

Info:

    curl localhost:8080/info

### Common Issues

#### My test won't run

    Execution failed for task ':test'.
    > No tests found for given includes:
    
Check your imports and ensure you're using JUnit5 `org.junit.jupiter.api.*` classes instead of the older JUnit4 `org.junit.*` ones.

#### Application repeatedly throws exceptions

    com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue 'nhais_mesh_inbound' in vhost '/', class-id=50, method-id=10)
    
You need to clear invalid messages from the inbound mesh queue.

* Browse: http://localhost:8161/
* Login: admin/admin
* Click 'Queues' tab
* On the `nhais_mesh_inbound` click 'Purge'
