#!/bin/bash

set -ex

curl -i --location --request POST 'http://localhost/fhir/Patient/$nhais.acceptance' \
     --data '@../../../src/intTest/resources/outbound_uat_data/acceptance/type1-birth-mandatory.fhir.json' \
     -H "Content-Type: application/json"

# Doesn't seem that ActiveMQ REST API can delivery contents of AMQP messages

#curl -i --location --request GET 'http://admin:admin@localhost:8161/api/message/nhais_mesh_outbound?type=queue&oneShot=true'

#curl -i --location -XGET 'http://admin:admin@localhost:8161/api/message?destination=queue://nhais_mesh_outbound&oneShot=true'