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

| Environment Variable             | Default                   | Description 
| ---------------------------------|---------------------------|-------------
| NHAIS_OUTBOUND_SERVER_PORT       | 80                        | The port on which the outbound FHIR REST API will run
| NHAIS_AMQP_BROKERS               | amqp://localhost:5672     | A comma-separated list of URLs to AMQP brokers for the outbound (to mesh) message queue (*)
| NHAIS_MESH_OUTBOUND_QUEUE_NAME   | nhais_mesh_outbound       | The name of the outbound (to mesh) message queue
| NHAIS_MESH_INBOUND_QUEUE_NAME    | nhais_mesh_inbound        | The name of the inbound (from mesh) message queue
| NHAIS_AMQP_USERNAME              |                           | (Optional) username for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_PASSWORD              |                           | (Optional) password for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_MAX_RETRIES           | 3                         | The number of times a request to the outbound (to mesh) broker(s) will be retried
| NHAIS_AMQP_RETRY_DELAY           | 100                       | Milliseconds delay between retries to the outbound (to mesh) broker(s)
| NHAIS_MONGO_DATABASE_NAME        | nhais                     | Database name for Mongo
| NHAIS_MONGO_URI                  | mongodb://localhost:27017 | Mongodb connection string
| NHAIS_LOG_LEVEL                  |                           | The desired logging level

(*) Active/Standby: The first broker in the list always used unless there is an error, in which case the other URLs will be used. At least one URL is required.

## Using AmazonMQ

Amazon gives their Amazon MQ endpoint with the scheme `amqp+ssl://` but this is not supported / recognised by Java.

You will need to change the scheme to `amqps://`

## Using ActiveMQ in a local container (for development)

Admin UI is at http://localhost:8161/
Login is admin/admin

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
    docker-compose up activemq mongodb nhais
    
There is also a container that will run all types of tests

    docker-compose up nhais-tests

### Running Tests

Ensure development dependencies are installed before running the tests

    pipenv install -d

#### Unit Tests

    pipenv run unittests
    
#### Component Tests

All component tests are annotated with "component" Tag.
Component tests will not be launched using gradle test task.
To run component tests you have to use command:

    ./gradlew componentTest

### Integration Tests

Integration tests are located in a separate source folder src/intTest.
To run the integration tests use:

    ./gradlew integrationTest


### Configuration

The following additional configuration is used by integration tests

| Environment Variable             | Default          | Description 
| ---------------------------------|------------------|-------------
| NHAIS_OUTBOUND_ADDRESS           | http://localhost | The URL where the NHAIS service can be accessed

**Prerequisites**

* Run dynamo and rabbitmq locally using docker-compose from repository root
* Set and export environment variables defined in `nhais-env-example.yaml`
* Run `main.py`


    pipenv run inttests

### IntelliJ Configuration 

Open integration-adaptor-nhais in IDE  

File → Project Structure → SDK → Add new Python SDK → Select Pipenv Environment and provide a path to the executable of my pipenv.

Select File → New → Module from existing sources

Point to NHAIS folder

Select “Create module from existing sources"

Click through wizard and select correct pipenv environment for NHAIS if it asks for one

Select main.py in root directory, click `configure pipenv interperator`

Now you can add configurations to run component. Just make sure configuration uses correct Python interpreter and set EnvFile:
`nhais-env.yaml` copy and paste example. 

Run → Edit configurations → plus arrow (add new configuration) → python 

Configuration tab:

Script path should be integration-adaptor-nhais → main.py

Working directory should be integration-adaptor-nhais

EnvFile tab:

Check box enable EnvFile

Add previously created yaml file `nhais-env.yaml`

### Management endpoints:
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

#### Application repeatedly throws exceptions about a missing queue

    com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue 'nhais_mesh_inbound' in vhost '/', class-id=50, method-id=10)
    
You need to create this queue.

* Browse: http://localhost:15672/
* Login: guest/guest
* Click 'Queues' tab
* Expand 'Add a queue'
* Name: nhais_mesh_inbound
* Durability: Durable
* Click 'Add queue'

The errors should stop without needing to restart the app.