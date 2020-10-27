# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.4.0] - 2020-10-26

- Added a workaround for an NHAIS issue where the DTM segment contains a timestamp is the past. The adaptor now reads its
 translationTimestamp from the interchange header.
- Encapsulate outboundState recep fields in sub-document. Updated the documentation for reporting on missing RECEPs. No
 data migration in place; to generate reports on pre-1.4.0 records follow the instructions from version 1.3.1.
- The inboundState and outboundState.recep documents now contain a 'processedTimestamp' as a record of when the adaptor 
 received and processed the transaction.
- Adds a ConversationId header to outbound API requests, inbound GP system message queue, related internal queues, 
 and database documents. Logs related to processing that transaction include the ConversationId value.
- Replaced an embedded trust store with the capability to download a trust store (Java Keystore) at start-up when 
 operating the adaptor requires non-public CA certificates. Presently only downloads from AWS S3 are supported. The use-case is for AWS Documentdb.
- Added additional logging and MESH request headers to meet compliance criteria for the MESH API.
- Added a terminology section to the OpenAPI document to disambiguate some confusing names of GP and HA identifiers.
- Renamed the NHAIS_MESH_CYPHER_TO_MAILBOX environment variable to NHAIS_MESH_RECIPIENT_MAILBOX_ID_MAPPINGS. Added more
 detailed documentation about how this variable should be set.
- Added a tip to the OPERATING.md document about configuring the adaptor to use Azure Service Bus

## [1.3.1] - 2020-09-24

- [Issue #244](https://github.com/nhsconnect/integration-adaptor-nhais/issues/244) Documentation added about preserving 
database contents and continuing an established HA/GP link
- Fixed issues with RECEP message header and DTM segment that caused NHAIS validation errors
- The adaptor now sends to NHAIS the '?' placeholder for empty first address lines. The adaptor removes the placeholder 
for inbound addresses with empty first lines.

## [1.3.0] - 2020-09-03

- Added DocumentDB TLS support
- All API error responses are now FHIR OperationOutcome

## [1.2.0] - 2020-08-19

### Added

- The adaptor now calls the MESH "Authenticate Mailbox" action before each polling cycle and before each sent message
- The adaptor now by default validates certificates used for the TLS connection to the MESH API
  - New environment variables described in [README.md](./README.md): `NHAIS_MESH_CERT_VALIDATION`, `NHAIS_MESH_SUB_CA`
- Time-to-live indexes added to the `outboundState` and `inboundState` collections
  - New environment variables described in [README.md](./README.md): `NHAIS_MONGO_TTL`, `NHAIS_COSMOS_DB_ENABLED`
  - [README.md](./README.md) notes some differences between TTL indexes in MongoDB and Cosmos DB

### Fixed

- Outbound API no longer accepts empty patient previous addresses
- [Issue #128](https://github.com/nhsconnect/integration-adaptor-nhais/issues/128) Acceptance type 3 (Transfer in) now requires the patient's previous address
- [Issue #127](https://github.com/nhsconnect/integration-adaptor-nhais/issues/127) All Acceptance types now require the patient's surname
- [Issue #225](https://github.com/nhsconnect/integration-adaptor-nhais/issues/225) Improved the documentation for the `NHAIS_MESH_CYPHER_TO_MAILBOX` environment variable
- [Issue #219](https://github.com/nhsconnect/integration-adaptor-nhais/issues/219) Improved the documentation for the `NHAIS_MESH_HOST` environment variable
- Upgraded a number of Java dependencies to fix CVEs
- The `NHS` segment of generated RECEP interchanges now contain the correct values for HA Cypher and GP Codes
- The MESH polling cycle now has a duration to prevent a previous cycle from overrunning into the next
  - New environment variable described in [README.md](./README.md): `NHAIS_MESH_POLLING_CYCLE_DURATION_IN_SECONDS`
  - Renamed environment variables described in [README.md](./README.md): `NHAIS_MESH_CLIENT_WAKEUP_INTERVAL_IN_MILLISECONDS`, `NHAIS_MESH_POLLING_CYCLE_MINIMUM_INTERVAL_IN_SECONDS`
- Restructured [README.md](./README.md) to make information easier to find and to remove duplication

## [1.1.0] - 2020-08-05

### Release Notes

The fix for [Issue #201](https://github.com/nhsconnect/integration-adaptor-nhais/issues/201) changes the names of some
properties in two database collections. Any existing collections should be dropped before using this version.

### Changed

Features:

- Validate that a MESH Mailbox ID has been configured for the HA Trading Partner Code before translating outbound transactions
- Improved logging of MESH API interactions and handling of messages with unsupported workflow ids
- Acknowledge (RECEP) inbound Close Quarter Notification transactions without producing a FHIR message or error

Non-functional:

- V&P Testing: RECEP Responder - NHAIS adaptor instance that responds with RECEP messages
- V&P Testing: Dockerised RECEP Responder
- V&P Testing: Batch inbound EDIFACT file generator
- V&P Testing: Batch inbound EDIFACT file sender
- V&P Testing: JMeter script for outbound transactions

Documentation:

- [REPORTING.md](./REPORTING.md) describes how to detect missing interchanges

### Fixed

- [Issue #201](https://github.com/nhsconnect/integration-adaptor-nhais/issues/201) Shortened Inbound and Outbound state 
key names to make them CosmosDB compliant.
- Removed extra trailing slash from MESH send message URI which prevented sending messages to MESH API

## [1.0.2] - 2020-07-28

### Changed

- Fixes a bug preventing messages from being sent to MESH

## [1.0.1] - 2020-07-27

### Added

- More detailed DEBUG logging for MESH API interactions
- Ability to control logging level using an environment variable
- Documentation for how to report on missing sequence numbers

### Changed

- "Workflow" section of README refers to relevant GP Links Specification chapter
- Suppress a duplicate key error that sometimes appears at startup

## [1.0.0] - 2020-07-21

### Added

- Outbound (GP->HA) Removal transaction - completed support for all fields
- Outbound (GP->HA) Deduction transaction - completed support for all fields
- Outbound (GP->HA) Acceptance transaction
    - Completed support for all fields
    - Either NHS Number or Birthplace are required for Acceptance types 2, 3, and 4
- Outbound (GP->HA) Acceptance transaction - completed support for all fields
- Inbound Amendment transaction
- Inbound Deduction transaction
- Inbound Deduction Request Rejection transaction
- Inbound FP69 Prior Notification transaction
- Inbound FP69 Flag Removal transaction

## [0.2.0] - 2020-07-09

### Added

- MESH API Integration
- Partial outbound (GP->HA) Deduction and Removal transactions
    - Only a "stub" transaction - not all fields translated to EDIFACT
    - Implemented:
        - GP Trading Partner Code
        - GP Code
        - Destination HA Cipher
- Partial outbound (GP->HA) Amendment transaction
    - Non-patch parameters
    - Patches for patient name fields
- Additional outbound (GP->HA) Acceptance transaction fields
    - Added support for patient name and postcode fields
- Support for inbound interchanges containing multiple messages and transactions
- Documentation for most (Amendment excluded) inbound transactions

### Fixed
- NIAD-383: Inbound interchanges using control characters (' + : ?) in data fields are now handled correctly.
- NIAD-340: When the application is run **using the docker-compose file provided** messages that cannot be processed are
    transferred to a dead-letter queue with the prefix "DLQ". Since this is managed by the broker __other deployments 
    must configure the broker as described in 
    [integration-adaptor-nhais/README.md](https://github.com/nhsconnect/integration-adaptor-nhais/blob/develop/README.md) 
    to handle errors appropriately.__
    
## [0.1.0] - 2020-06-22

### Added
- Outbound (GP->HA) Acceptance transaction
    - All four acceptance types (type 5 excluded)
    - Mandatory fields only
- Inbound (HA->GP) Approval transaction
- Inbound (HA->GP) Rejection (Wrong HA) transaction
- Documentation of formats for inbound message queue messages

### Changed
- OpenAPI Specification (`/specification`) for Acceptance API
- Examples (see [README](./README.md)) for many transaction types incorporated into user acceptance tests
- RECEP message id now stored in state database collections
- Mongodb database connection may be configured using a connection string or individual properties

## [0.0.1] - 2020-06-10
### Added
- First release of GP Links - NHAIS Adaptor
- Only /healthcheck endpoint is officially supported
- Manages stateful concerns of GP Links messaging
- API stubs that are subject to change

### Changed
n/a

### Removed
n/a

### Known Issues
- NIAD-385: The adaptor may on occasion log an exception (related to toString) but continue to operate as expected
