#!/bin/bash

set -ex

curl -i --location --request POST 'http://localhost/fhir/Patient/$nhais.deduction' \
     --data '@../../../src/intTest/resources/outbound_uat_data/deduction/stub.fhir.json' \
     -H "Content-Type: application/json"