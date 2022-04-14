
# Quick reference
- Maintained by: NHS Digital
- Where to get help: https://github.com/nhsconnect/integration-adaptor-nhais
- Where to file issues: https://github.com/nhsconnect/integration-adaptor-nhais/issues

# What is the GP Links - NHAIS Adaptor?
A pre-assured implementation of NHAIS/GP Links, that encapsulates the details of GP Links Registration and RECEP 
messaging and provides a simple interface to allow HL7 messages to be sent to and received from the NHAIS instances 
via MESH.

# How to use this image
## Pre-requisites

To get running make sure you have an OpenTest environment setup. A "fake-mesh" container is provided for local testing
without OpenTest.

## Clone the repository
```bash
$ git clone https://github.com/nhsconnect/integration-adaptor-nhais.git
```

## Find the test scripts folder
```bash
$ cd integration-adaptor-nhais/test-scripts
```

Each release has its own folder. Use the scripts for the specific release being tested.

```bash
$ cd 1.4.2
```

## Configure the application

Configure the application using a `.example.sh` file as a template:

```bash
$ cp export-env-vars.fake-mesh.example.sh export-env-vars.sh
```

which will work with the fake-mesh container provided

**or**

```bash
$ cp export-env-vars.opentest.example.sh export-env-vars.sh
```

and populate the variables in this file with the details provided when you signed up for OpenTest.

## Start it up
```bash
$ cd 1.4.2
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