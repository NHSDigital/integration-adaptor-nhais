#!/bin/bash

set -ex

curl -i --location --request POST 'http://localhost/fhir/Patient/$nhais.removal' \
     --data '@../../../src/intTest/resources/outbound_uat_data/removal/app-j-1.fhir.ignore.json' \
     -H "Content-Type: application/json"