# NHAIS Adaptor - Developer Notes
The following sections are intended to provide the necessary info on how to configure and run the NHAIS adaptor.

Environment Variables are used throughout application, an example can be found in `nhais-env-example.yaml`. 

## Pre-requisites

* Install an Java JDK 11. AdoptOpenJdk is recommended: https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot
* Download Lombok plugin : https://plugins.jetbrains.com/plugin/6317-lombok
* MongoDB: `docker-compose up mongodb`
* RabbitMQ: `docker-compose up activemq`

## Developer setup:

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

| Environment Variable             | Default               | Description 
| ---------------------------------|-----------------------|-------------
| NHAIS_OUTBOUND_SERVER_PORT       | 80                    | The port on which the outbound FHIR REST API will run
| NHAIS_AMQP_BROKERS               | amqp://localhost:5672 | A comma-separated list of URLs to AMQP brokers for the outbound (to mesh) message queue (*)
| NHAIS_MESH_OUTBOUND_QUEUE_NAME   | nhais_mesh_outbound   | The name of the outbound (to mesh) message queue
| NHAIS_MESH_INBOUND_QUEUE_NAME    | nhais_mesh_inbound    | The name of the inbound (from mesh) message queue
| NHAIS_AMQP_USERNAME              |                       | (Optional) username for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_PASSWORD              |                       | (Optional) password for the amqp server for outbound (to mesh) message queue
| NHAIS_AMQP_MAX_RETRIES           | 3                     | The number of times a request to the outbound (to mesh) broker(s) will be retried
| NHAIS_AMQP_RETRY_DELAY           | 100                   | Milliseconds delay between retries to the outbound (to mesh) broker(s)
| NHAIS_MONGO_DATABASE_NAME        | nhais                 | Database name for Mongo
| NHAIS_MONGO_USERNAME             |                       | (Optional) username for Mongo
| NHAIS_MONGO_PASSWORD             |                       | (Optional) password for Mongo
| NHAIS_MONGO_HOST                 | localhost             | Host for Mongo
| NHAIS_MONGO_PORT                 | 27017                 | Port for Mongo
| NHAIS_LOG_LEVEL                  |                       | The desired logging level

(*) Active/Standby: The first broker in the list always used unless there is an error, in which case the other URLs will be used. At least one URL is required.

## Using AmazonMQ

Amazon gives their Amazon MQ endpoint with the scheme `amqp+ssl://` but this is not supported / recognised by Java.

You will need to change the scheme to `amqps://`

## Using ActiveMQ in a local container (for development)

Admin UI is at http://localhost:8161/
Login is admin/admin

## Running

* Set and export environment variables defined in `nhais-env-example.yaml`
* NOTE: Enfile does not appear to work in IntelliJ / Grade. You will need to set each required variable 
  (see application.yml) in the run configuration if you use these.
* Run `uk.nhs.digital.nhsconnect.nhais.IntegrationAdaptorNhaisApplication`

## Running with Docker Compose

    docker-compose build
    docker-compose up activemq mongodb nhais
    
There is also a container that will run all types of tests

    docker-compose up nhais-tests

## Running Tests

Ensure development dependencies are installed before running the tests

    pipenv install -d

### Unit Tests

    pipenv run unittests
    
### Component Tests

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