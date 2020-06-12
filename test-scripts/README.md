
# Quick reference
- Maintained by: NHS Digital
- Where to get help: https://github.com/nhsconnect/integration-adaptor-nhais
- Where to file issues: https://github.com/nhsconnect/integration-adaptor-nhais/issues

# What is the NHAIS Adaptor?
A pre-assured implementation of NHAIS/GP Links, that encapsulates the details of GP Links Registration and RECEP messaging and provides a simple interface to allow HL7 messages to be sent to and received from the NHAIS instances via MESH.

# How to use this image
## Pre-requisites
To get running make sure you have an OpenTest environment setup.

Note: OpenTest not required for release 0.0.1

## Clone the repository
```bash
$ git clone https://github.com/nhsconnect/integration-adaptor-nhais.git
```

## Find the test scripts folder
```bash
$ cd integration-adaptor-nhais/test-scripts
```

## Setup your OpenTest details
Set up your OpenTest details using export-env-vars.sh.example as a template:
```bash
$ cp export-env-vars.sh.example export-env-vars.sh
```
Populate the variables in this file with the details provided when you signed up for OpenTest.

Note: OpenTest not required for release 0.0.1

## Start it up
```bash
$ cd 0.0.1
$ ./run.sh
```

You can verify that all the containers defined in the docker-compose.yml file in that folder are running:
```bash
$ docker-compose ps
```

## Start testing!

There are shell scripts in each of the release version folders that provide examples on how to structure your tests.

## Stopping the adaptor
```bash
$ docker-compose down
```