# NHAIS Adaptor - Developer Notes
The following sections are intended to provide the necessary info on how to configure and run the NHAIS adaptor.

Environment Variables are used throughout application, an example can be found in `nhais-env-example.yaml`. 

## Pre-requisites

* Install an Java JDK 11. AdoptOpenJdk is recommended: https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot
* MongoDB: `docker-compose up mongodb`
* RabbitMQ: `docker-compose up rabbitmq`

## Developer setup:

Open integration-adaptor-nhais -> click pop-up that appears (import gradle daemon)

Check the following:

Project structure   -> SDKs -> add new SDK -> select adoptopenjdk-11.jdk/Contents/Home
                    -> Project SDK -> java 11 (11.0.7)
                    -> Module SDK -> java 11 (11.0.7)
                    
To run in dev env, navigate to: IntegrationAdaptorNhaisApplication, right click, run() main.

## Configuration

The service is configured using environment variables. Variables without a default value and not marked optional are *required* to be provided
when the service is run.

| Environment Variable             | Default  | Description 
| ---------------------------------|----------|-------------
| NHAIS_OUTBOUND_SERVER_PORT       | 80       | The port on which the outbound FHIR REST API will run
| NHAIS_AMQP_BROKERS               |          | A comma-separated list of URLs to AMQP brokers for the outbound (to mesh) message queue (*)
| NHAIS_MESH_OUTBOUND_QUEUE_NAME   |          | The name of the outbound (to mesh) message queue
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

## Running

* Set and export environment variables defined in `nhais-env-example.yaml`
* NOTE: Enfile does not appear to work in IntelliJ / Grade. You will need to set each required variable 
  (see application.yml) in the run configuration if you use these.
* Run `uk.nhs.digital.nhsconnect.nhais.IntegrationAdaptorNhaisApplication`

## Running with Docker Compose

    docker-compose build
    docker-compose up rabbitmq dynamodb nhais
    
There is also a container that will run all types of tests

    docker-compose up nhais-tests

## Running Tests

Ensure development dependencies are installed before running the tests

    pipenv install -d

### Unit Tests

    pipenv run unittests
    
### Component Tests

Prerequisites

* Run dynamo locally using docker-compose from repository root
* Set and export environment variables defined in `nhais-env-example.yaml`


    pipenv run componenttests
    
### Integration Tests

### Configuration

The following additional configuration is used by integration tests

| Environment Variable             | Default          | Description 
| ---------------------------------|------------------|-------------
| NHAIS_OUTBOUND_ADDRESS           | http://localhost | The URL where the NHAIS service can be accessed

### Prerequisites

* Run dynamo and rabbitmq locally using docker-compose from repository root
* Set and export environment variables defined in `nhais-env-example.yaml`
* Run `main.py`


    pipenv run inttests

## Developer setup 

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

