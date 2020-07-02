#!/bin/bash

set -ex

curl -i --location --request POST 'http://localhost/fhir/Patient/$nhais.deduction' \
     --data '@../../../src/intTest/resources/outbound_uat_data/deduction/app-j-1.fhir.ignore.json' \
     -H "Content-Type: application/json"