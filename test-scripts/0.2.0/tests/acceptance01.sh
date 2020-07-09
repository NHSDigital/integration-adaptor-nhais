#!/bin/bash

set -ex

curl -i --location --request POST 'http://localhost/fhir/Patient/$nhais.acceptance' \
     --data '@../../../src/intTest/resources/outbound_uat_data/acceptance/type2-first-mandatory.fhir.json' \
     -H "Content-Type: application/json"
