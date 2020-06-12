# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.0.1] - 2019-06-10
### Added
- First release of NHAIS Adaptor
- Only /healthcheck endpoint is officially supported
- Manages stateful concerns of GP Links messaging
- API stubs that are subject to change

### Changed
n/a

### Removed
n/a

### Known Issues
- If an invalid message is published to the `nhais_mesh_inbound` when the application is run **using the docker-compose
  file provided** the application will log errors indefinitely. Purging this queue using the Active MQ web console will
  stop the errors. __Other deployments should use a broker configured as per the README.md to avoid this issue.__