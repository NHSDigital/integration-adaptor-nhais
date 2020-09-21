# Operating

This document contains requirements and tips for operation the adaptor in a production environment.

# AMQP Message Broker Requirements

* The broker must be configured with a limited number of retries and deadletter queues
* It is the responsibility of the GP supplier to configure adequate monitoring against the deadletter queues that allows ALL undeliverable messages to be investigated fully.
* The broker must use persistent queues to avoid loss of data
* The GP System must persist the relevant transaction data before acknowledging the message from the queue to avoid loss of data

**Using AmazonMQ**

* A persistent broker (not in-memory) must be used to avoid data loss.
* A configuration profile that includes settings for [retry and deadlettering](https://activemq.apache.org/message-redelivery-and-dlq-handling.html) must be applied.
* AmazonMQ uses the scheme `amqp+ssl://` but this **MUST** be changed the to `amqps://` when configuring the adaptor.

**Using Azure Service Bus**

* The ASB must use [MaxDeliveryCount and dead-lettering](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-dead-letter-queues#exceeding-maxdeliverycount)

# Mongodb Database Requirements

* The NHAIS Adaptor and NHAIS system communications synchronise through a sequence number mechanism
* The Mongodb database preserves this synchronisation
* Deleting the mongodb database and/or its collections will break the link with the NHAIS system

**Amazon Document DB**

In the "Connectivity & security" tab of the cluster a URI is provided to "Connect to this cluster with an application".
Replace <username>:<insertYourPasswordHere> with the actual mongo username and password to be used by the application.
The value of `NHAIS_MONGO_URI` should be set to this value. Since the URI string contains credentials we recommend that
the entire value be managed as a secured secret.

The user must have the `readWrite` role or a custom role with specific privileges.

**Azure Cosmos DB**

Follow Azure documentation on Cosmos DB's API for MongoDB.

## Time-to-live Indexes

The adaptor creates TTL (time to live) indexes on the `outboundState` and `inboundState` collections to automatically 
remove old documents. The variable `NHAIS_MONGO_TTL` described above controls the duration. There are differences between
how TTL indexes work between MongoDb and Azure Cosmos DB. When using Cosmos the `NHAIS_COSMOS_DB_ENABLED` flag must be true.

**TTL Indexes in MongoDB / AWS Document DB**

The property `translationTimestamp` is indexed. For outbound, this is the timestamp when the adaptor translates FHIR 
into EDIFACT and is the timestamp enclosed in the EDIFACT interchange sent to NHAIS. For inbound, this is the 
timestamp enclosed within the EDIFACT interchange received from NHAIS.

**TTL Indexes in Azure Cosmos DB**

[TTL Index in Cosmos](https://docs.microsoft.com/en-us/azure/cosmos-db/mongodb-time-to-live) are limited in that only 
a specific `_ts` property may have this index. The _ts property "is a system generated property (that) specifies the 
last updated timestamp of the resource". ([Reference](https://docs.microsoft.com/en-us/rest/api/cosmos-db/databases))

For outbound, each document is "last updated" when the adaptor processes the inbound RECEP for that transaction. For
inbound each document is "last updated" after publishing the FHIR message to the inbound GP System message queue.

The impact is that documents in Cosmos may live slightly longer than those stored in Mongo.

## AWS DocumentDB TLS configuration

AWS DocumentDB uses a private CA certificate and therefore requires a custom keystore to manage the CA certificates effectively.

To use TLS, it has to be enabled in the DocumentDB instance with the Mongo connection string containing the `tls=true`. 
Additionally, the environment variable `NHAIS_MONGO_DOCUMENTDB_TLS_ENABLED` must be set to true. 
For more information on TLS configuration in MongoDB see [TLS options for Mongo connection string](https://docs.mongodb.com/manual/reference/connection-string/#tls-options).

SSH tunneling might require adding the connection string option: `tlsAllowInvalidHostnames=true` (as CA would try to resolve localhost as hostname).
This option should only be used for local tests as this might create a vulnerability.

Use of DocumentDB TLS requires java trust store to be provided manually. Instructions on how to create trust store can be found here: 
[Connect to Document DB programmatically](https://docs.aws.amazon.com/documentdb/latest/developerguide/connect_programmatically.html#connect_programmatically-tls_enabled) 

There are two ways to configure trust store depending on how the adaptor is ran:
1. Gradle run
   
   Set absolute path to trust store file in `NHAIS_MONGO_TRUST_STORE_PATH` environment variable.
   
2. Docker

   Create folder `truststore` where docker-compose.yml file is. Then place trust store file inside this folder.
   Build docker image using `docker-compose build nhais`. As docker creates image it copies all files from `./truststore/` folder 
   (it uses relative path from `docker-compose.yml` file) to docker image filesystem as `/truststore/`
   Next step is to specify trust store filename using `NHAIS_MONGO_TRUST_STORE_PATH` environment variable in docker-compose.yml file. 
   It has to be prepended with `/truststore/` (ex. `NHAIS_MONGO_TRUST_STORE_PATH=/truststore/aws-docdb-truststore.jks`).
   
Both options require `NHAIS_MONGO_DOCUMENTDB_TLS_ENABLED` to be set to true and `NHAIS_MONGO_TRUST_STORE_PASSWORD` set to trust store password
   

# MESH Requirements

**Note**: The "Development" section of the README refers to a fake-mesh component. fake-mesh is **not** part of the 
adaptor  solution and should only be used to assist local development.

NHSD manage access to MESH, allocate mailboxes, and provide connection details / credentials.

# Management Endpoints

[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) provides
three management endpoints:

* /healthcheck
* /metrics
* /info

# Monitoring GP Links messaging state

There are two Mongo collections recording the state of GP Links transactions:

- `outboundState` records every transaction sent by the adaptor (GP->HA)
- `inboundState` records every transaction received by the adaptor (HA->GP)

These collections should be monitored to identify any transactions that may be missing.

See [REPORTING.md](./REPORTING.md) for details about how to run these reports.

# Linking a GP Practice to an NHAIS system

The NHAIS Adaptor and NHAIS system communications synchronise through a sequence number mechanism. Linking a GP 
Practice to an NHAIS system which have never previously exchanged messages requires no additional setup for 
synchronisation. All the sequences begin at 1, and the adaptor will start them automatically.

In the case that a new market entrant GP System takes over from an incumbent system the new system must pick up the 
sequences where the incumbent left off. For every GP/NHAIS link established, the incumbent supplier or NHAIS operator 
must advise the following:

* Most recently used Send Interchange Sequence (SIS) number, GP -> HA
* Most recently used Send Message Sequence (SMS) number, GP -> HA
* Most recently used Transaction Number (TN), GP -> HA

For each GP/NHAIS pair the following documents must be inserted into the `outboundSequenceId` collection of the 
adaptor's database. The angle-bracketed values must be replaced (including the brackets) with the relevant data items.
The `_id` property should have the type `String`, and the `sequenceNumber` property should have the type `int32`. Any 
existing documents with the same `_id` must be replaced.

    {
        _id: 'SIS-<GP Link (Trading Partner) Code>-<HA Link (Trading Partner) Code>',
        sequenceNumber: <Send Interchange Sequence (SIS) number>
    }
    
    {
        _id: 'SMS-<GP Link (Trading Partner) Code>-<HA Link (Trading Partner) Code>',
        sequenceNumber: <Send Message Sequence (SMS) number>
    }
 
    {
        _id: 'TN-<GP Link (Trading Partner) Code>',
        sequenceNumber: <Send Message Sequence (SMS) number>
    }
