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

Ensure you have Pipenv installed and on your path, then within NHAIS directory, run:

    pipenv install

### Configuration

The service is configured using environment variables. Variables without a default value and not marked optional are *required* to be provided
when the service is run.

| Environment Variable             | Default  | Description 
| ---------------------------------|----------|-------------
| NHAIS_OUTBOUND_SERVER_PORT       | 80       | The port on which the outbound FHIR REST API will run
| NHAIS_OUTBOUND_QUEUE_BROKERS     |          | A comma-separated list of URLs to AMQP brokers for the outbound (to mesh) message queue (*)
| NHAIS_OUTBOUND_QUEUE_NAME        |          | The name of the outbound (to mesh) message queue
| NHAIS_OUTBOUND_QUEUE_USERNAME    |          | (Optional) username for the amqp server for outbound (to mesh) message queue
| NHAIS_OUTBOUND_QUEUE_PASSWORD    |          | (Optional) password for the amqp server for outbound (to mesh) message queue
| NHAIS_OUTBOUND_QUEUE_MAX_RETRIES | 3        | The number of times a request to the outbound (to mesh) broker(s) will be retried
| NHAIS_OUTBOUND_QUEUE_RETRY_DELAY | 100      | Milliseconds delay between retries to the outbound (to mesh) broker(s)
| NHAIS_DYNAMODB_ENDPOINT_URL      |          | URL of dynamodb instance (if used)
| NHAIS_PERSISTENCE_ADAPTOR        | dynamodb | To specify the database adaptor used
| NHAIS_LOG_LEVEL                  |          | The desired logging level
| AWS_ACCESS_KEY_ID                |          | The AWS Access Key ID for DynamoDB (if used). If using local dynamo can be set to 'test'
| AWS_SECRET_ACCESS_KEY            |          | The AWS Secret Acess Key for DynamoDB (if used). If using local dynamo can be set to 'test'

(*) Active/Standby: The first broker in the list always used unless there is an error, in which case the other URLs will be used. At least one URL is required.

### Running

* Run dynamo and rabbitmq locally using docker-compose; or set environment variables to target desired instances of these
* Set and export environment variables defined in `nhais-env-example.yaml`
* Run `main.py`

### Running with Docker Compose

    docker-compose build
    docker-compose up rabbitmq dynamodb nhais
    
There is also a container that will run all types of tests

    docker-compose up nhais-tests

### Running Tests

Ensure development dependencies are installed before running the tests

    pipenv install -d

#### Unit Tests

    pipenv run unittests
    
#### Component Tests

**Prerequisites**

* Run dynamo locally using docker-compose from repository root
* Set and export environment variables defined in `nhais-env-example.yaml`


    pipenv run componenttests
    
#### Integration Tests

**Configuration**

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