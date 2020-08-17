# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

- Calling MESH authentication on mailbox scan and before sending MESH message
- Time-to-live indexes for both MongoDB and Cosmos DB
- New environment variables: `NHAIS_MONGO_TTL`, `NHAIS_COSMOS_DB_ENABLED` (see README.md for details)
- Empty patient previous address is not allowed anymore
- A patient previous address is required for acceptance type 3 (Transfer in)
- A patient surname is required for all acceptance types
- Renamed two MESH environment variables and added one new variable. Added more detailed descriptions in README of these variables.

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
