#!/bin/bash

set -ex

curl -i --location --request POST 'http://internal-nia-ptl-nhais-ecs-lb-1306825707.eu-west-2.elb.amazonaws.com/fhir/Patient/$nhais.acceptance' \
     --data '@acceptance-approval.json' \
     -H "Content-Type: application/json"
