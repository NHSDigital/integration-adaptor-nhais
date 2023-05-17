
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

## OpenTest Setup

To use OpenTest environment, please first follow the 'Before you start' section at the following url to register
for access to OpenTest

```
https://digital.nhs.uk/services/path-to-live-environments/opentest-environment
```

You will then receive an email with required details to connect to the environment. Populate `export-env-vars.sh` 
created earlier with the provided details.  If `NHAIS_MESH_SHARED_KEY` is not provided in the email, the default for 
this value is `BackBone`.

# Windows OpenTest VPN

Windows should support TAP and allow you to use the OpenVPN client.

1. Download the Legacy version of the OpenVPN client from: `https://openvpn.net/client/client-connect-vpn-for-windows/`.
2. In the received instructions, a `.ovpn` will be attached to the email.  Download this file and import it.
3. Clicking connect should then allow access to the OpenTest environment.

# MacOS OpenTest VPN

This is more complicated as Mac Os BigSur and later versions do not allow TAP to be used, which is required for 
OpenTest.  It is possible to use an alternative client to connect, with a little configuration.

1. Download the latest stable release of the TunnelBlick OpenVpn Client from: `https://tunnelblick.net/downloads.html`. 
2. Once installed, the VPN client will automatically start and the icon will appear in the running apps area.
3. use the following instruction to install Kexts which allows us to use TAP (please note this requires a restart): `https://tunnelblick.net/cKextsInstallation.html#intel-big-sur`.
4. Download the OpenVPN configuration file (.ovpn) attached to the received setup email.
5. Drag and drop the downloaded .ovpn file onto the taskbar icon for TunnelBlick.
6. Click connect in the window which appears.

*NOTE*: Occasionally the client will drop and docker logs for the NHAIS adapter will display errors that is temporarily 
unable to access the the requested URL.  In this case, you will need to disconnect and reconnect the VPN client.