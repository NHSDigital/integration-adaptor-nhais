# GP Links - NHAIS Adaptor

NHAIS is a system that allows General Practice (GP) Surgeries to keep their patient registration and demographics data 
in sync with the regional Health Authorities (HA). Since the creation of this service the regional or area health 
authorities (approx 80) have since been replaced by a fewer number of successor organisations. There is however still a 
notion of every GP Practice’s patient being registered with one of the HAs.

See the [Resources](#resources) section for links to the underlying services and standards.

## Adaptor Scope

The patient registration and demographics portion of NHAIS is called HA/GP Links. NHAIS supports some features in 
addition to GP Links but these are out of scope for the GP Links - NHAIS Adaptor project.

HA/GP Links messaging is comprised of several types "transactions" used to update and reconcile patient lists and 
patient demographic data. The following transaction types are supported by the GP Links - NHAIS Adaptor:

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

The goal of the GP Links - NHAIS Adaptor is to remove the requirement for a GP System to handle the complexities of EDIFACT and 
MESH messaging.

## Workflows

Chapter 3 of the GP Links Specification describes each transaction type including workflow and processing diagrams. In 
this document "OUT-GOING" is the same is Outbound (GP -> HA) and  "IN-COMING" is the same as Inbound (HA -> GP). 
Transaction names and field names are consistent between the GP Links specification and the adaptor's documentation.

## Adaptor API

### Outbound (GP -> HA)

The GP System will send outbound messages using a HL7 FHIR R4 REST API: [Outbound (GP -> HA) OpenAPI Specification](specification/nhais-adaptor.yaml)

### Inbound (HA -> GP)

The GP System will receive inbound messages from an AMQP message queue. See [INBOUND.md](./INBOUND.md) for documentation
of the message formats.

### Examples

Examples of outbound request to the Adaptor's API and inbound reply and unsolicited messages to the Inbound Supplier MQ
are provided as part of the adaptor's User Acceptance Tests.

Examples with filenames containing `app-j-` are copied from the _GP SYSTEMS SPECIFICATION - APPENDIX J - SAMPLE 
REGISTRATION EDIFACT MESSAGES_. Examples with filenames containing `live-` are sanitised copies of recent NHAIS live 
service transactions.

#### Outbound Examples

The [outbound_uat_data](src/intTest/resources/outbound_uat_data) folder contains examples of outbound 
(GP->HA) transactions. There is a sub-folder for each transaction type. Within each of those folder are sets of 2-3 
files for each example:

* `<example-id>.fhir.json`: The JSON payload sent from the GP System to the Adaptor.
* `<example-id>.edifact.dat`: The EDIFACT file sent from the adaptor to the NHAIS instance for the request
* `<example-id>.notes.txt`: (If provided) a textual descriptions of the transaction if one was provided by the source data set

**Note**: Some files are named `.ignore.dat` / `.ignore.json`. These are examples for transaction types that are not yet 
implemented. The `ignore` tells our test quite to not run tests for these examples.

#### Inbound Examples

The [inbound_uat_data](src/intTest/resources/inbound_uat_data) folder contains examples of inbound 
(HA->GP) transactions. There is a sub-folder for each transaction type. Within each of those folder are sets of 2-3 
files for each example:
                       
* `<example-id>.fhir.json`: The JSON message published into the Inbound Supplier MQ.
* `<example-id>.edifact.dat`: The EDIFACT file from an NHAIS instance to the adaptor
* `<example-id>.txt`: (If provided) a textual descriptions of the transaction if one was provided by the source data set

**Note**: Some files are named `.ignore.dat` / `.ignore.json`. These are examples for transaction types that are not yet 
implemented. The `ignore` tells our test quite to not run tests for these examples.

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

The following sections are intended to provide the necessary info on how to configure and run the GP Links - NHAIS adaptor.

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

Debug database and queues for NHAIS:

[NHAIS Diagram with key](/documentation/nhais_diagram_plus_key.jpeg)

### Mongo DB

To view data in MongoDB:

Download Robo 3T
https://robomongo.org/

Open Robo 3T -> Create new connection with details as below:

- Type: Direct Connection
- Name: nhais
- Address: localhost : 27017

View adaptor collections by navigating to nhais -> collections -> (select any table)

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
| NHAIS_LOGGING_LEVEL                | INFO                     | Application logging level. One of: DEBUG, INFO, WARN, ERROR
| NHAIS_AMQP_BROKERS                 | amqp://localhost:5672     | A comma-separated list of URLs to AMQP brokers for the outbound (to mesh) message queue (*)
| NHAIS_MESH_OUTBOUND_QUEUE_NAME     | nhais_mesh_outbound       | The name of the outbound (to mesh) message queue
| NHAIS_MESH_INBOUND_QUEUE_NAME      | nhais_mesh_inbound        | The name of the inbound (from mesh) message queue
| NHAIS_GP_SYSTEM_INBOUND_QUEUE_NAME | nahis_gp_system_inbound   | The name of the inbound (to gp system) message queue
| NHAIS_AMQP_USERNAME                |                           | (Optional) username for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_PASSWORD                |                           | (Optional) password for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_MAX_REDELIVERIES        | 3                         | The number of times an message will be retried to be delivered to consumer. After exhausting all retires, it will be put on DLQ.<queue_name> dead letter queue

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

MESH configuration is done using environment variables:

| Environment Variable             | Default                   | Description 
| ---------------------------------|---------------------------|-------------
| NHAIS_MESH_MAILBOX_ID            | N/A                       | Your MESH mailbox id (sender)
| NHAIS_MESH_MAILBOX_PASSWORD      | N/A                       | The password for MAILBOX_ID
| NHAIS_MESH_SHARED_KEY            | N/A                       | Shared key used to generate auth token. Provided by MESH operator (OpenTest, PTL, etc)
| NHAIS_MESH_HOST                  | N/A                       | Hostname of MESH service
| NHAIS_MESH_CERT_VALIDATION       | false                     | Certificate validation for SSL connection
| NHAIS_MESH_ENDPOINT_CERT         | N/A                       | Certificate used for connecting to MESH (content of it)
| NHAIS_MESH_ENDPOINT_PRIVATE_KEY  | N/A                       | Private key of certificate used for connecting to MESH (content of it)
| NHAIS_MESH_SUB_CA                | N/A                       | Sub CA certificate for cert validation. Not needed if NHAIS_MESH_CERT_VALIDATION is false
| NHAIS_MESH_CYPHER_TO_MAILBOX     | N/A                       | HA cypher (HA trading partner code) to MESH mailbox mapping (one per line) ex. cypher=mailbox
| NHAIS_SCHEDULER_ENABLED          | true                      | Enables/disables automatic MESH message downloads

The following two variables control how often the adaptor checks its MESH mailbox for new messages. To prevent
duplicate processing of MESH messages only one instance of the adaptor downloads messages at a time. The MESH API
specifies that a MESH mailbox should not be checked more than once every five minutes. The variable 
`NHAIS_SCAN_MAILBOX_DELAY_IN_SECONDS` controls how often the adaptor will check its mailbox for new messages. After
checking the mailbox for new messages the same adaptor instance will proceed to download and acknowledge all of the 
new messages.

A database lock is used to prevent more than one instance of the adaptor from downloading messages at the same time.
The variable `NHAIS_SCAN_MAILBOX_INTERVAL_IN_MILLISECONDS` controls how often each instance of the adaptor will attempt
to obtain this lock.

| Environment Variable             | Default                   | Description 
| ---------------------------------|---------------------------|-------------
| NHAIS_SCAN_MAILBOX_INTERVAL_IN_MILLISECONDS | 60000          | Polling frequency (in milliseconds) to obtain database lock
| NHAIS_SCAN_MAILBOX_DELAY_IN_SECONDS | 300                    | Maximum frequency for checking for and downloading new MESH messages


For local test scripts see [mesh/README.md](/mesh/README.md)

### OpenTest

TODO

### Fake MESH

Documentation and Source Code: https://github.com/mattd-kainos/fake-mesh

A fake-mesh image is published to [nhsdev Docker Hub](https://hub.docker.com/repository/docker/nhsdev/fake-mesh) and 
can be run from this project's [docker-compose.yml](./docker-compose.yml) file.

    docker-compose up fake-mesh

## Running

* Set and export environment variables defined in `nhais-env-example.yaml`
* NOTE: Enfile does not appear to work in IntelliJ / Grade. You will need to set each required variable 
  (see application.yml) in the run configuration if you use these.
* Run `uk.nhs.digital.nhsconnect.nhais.IntegrationAdaptorNhaisApplication`

### Running with Docker Compose

    docker-compose build
    docker-compose up
    
### Running with Docker Compose and Load Balancer

Docker compose configuration allows running multiple instances of NHAIS application with an NGINX load balancer in front using round robin routing by default.

    docker-compose build
    docker-compose -f docker-compose.yml -f docker-compose.lb.override.yml up --scale nhais=3

This command will spawn 3 instances of NHAIS and an LB working on port 8080
There are 2 options on how to change the scale number while all services are running:

* stop and start the whole cluster with new scale value

or

* run the same "up" command with new scale value while the cluster is running and then restart the LB container so it will be aware of instance count change 

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

## Operating the Adaptor

### Monitoring GP Links messaging state

There are two Mongo collections recording the state of GP Links transactions:

- `outboundState` records every transaction sent by the adaptor (GP->HA)
- `inboundState` records every transaction received by the adaptor (HA->GP)

These collections should be monitored to identify any transactions that may be missing.

See [REPORTING.md](./REPORTING.md) for details about how to run these reports.