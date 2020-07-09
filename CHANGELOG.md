# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] - 2020-07-09

### Added

- MESH API Integration
- Partial outbound (GP->HA) Deduction transaction
    - Only a "stub" transaction - not all fields translated to EDIFACT
- Partial outbound (GP->HA) Removal transaction
    - Only a "stub" transaction - not all fields translated to EDIFACT
- Partial outbound (GP->HA) Amendment transaction
    - Non-patch parameters
    - Patches for patient name fields
- Partial outbound (GP->HA) Acceptance transaction
    - Added support for patient name and postcode fields
- Support for inbound interchanges containing multiple messages and transactions
- Documentation for most (Amendment excluded) inbound transactions

### Fixed
- NIAD-383: Inbound interchanges using control characters (' + : ?) in data fields are now handled correctly.

    
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
- NIAD-340: If an invalid message is published to the `nhais_mesh_inbound` queue when the application is run **using the 
  docker-compose file provided** the application will log errors indefinitely. Purging this queue using the Active MQ 
  web console will stop the errors. __Other deployments should use a broker configured as per the README.md to avoid 
  this issue.__
- NIAD-385: The adaptor may on occasion log an exception (related to toString) but continue to operate as expected
- NIAD-383: The control characters (' + : ?) that must be escaped in EDIFACT are not handled correctly. Outbound 
  transactions using  these characters will produce invalid EDIFACT and inbound transactions using these characters will
  not be parsed correctly.