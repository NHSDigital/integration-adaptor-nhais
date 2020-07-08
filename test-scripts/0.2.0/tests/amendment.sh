#!/bin/bash

set -ex

curl -i --location --request PATCH 'http://localhost/fhir/Patient/9999999999' \
     --data '@../../../src/intTest/resources/outbound_uat_data/amendment/stub.fhir.json' \
     -H "Content-Type: application/json"
