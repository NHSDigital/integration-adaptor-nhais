#!/usr/bin/env bash

source env.sh

TO_MAILBOX=${TO_MAILBOX:-${MAILBOX}}
TOKEN=''
HOST="${HOST:-msg.opentest.hscic.gov.uk}"
# "Your endpoint private key" from your OpenTest registration e-mail
OPENTEST_ENDPOINT_PRIVATE_KEY="${OPENTEST_ENDPOINT_PRIVATE_KEY:-${HOME}/opentest.private.key}"
# "Your endpoint certificate" from your OpenTest registration e-mail
OPENTEST_ENDPOINT_CERT="${OPENTEST_ENDPOINT_CERT:-${HOME}/opentest.endpoint.cert}"
CURL_FLAGS='-k' # insecure, disable cert validation for fake-mesh

create_token() {
  local nonce
  local nonce_count
  local timestamp
  local hash_content
  local hash_value
  nonce=$(uuidgen)
  nonce=$(echo "${nonce}" | tr '[:upper:]' '[:lower:]')  # to lowercase
  nonce_count='001'
  timestamp=$(date +"%Y%m%d%H%M")
  hash_content="${MAILBOX}:${nonce}:${nonce_count}:${MAILBOX_PASSWORD}:${timestamp}"
  hash_value=$(echo -n "${hash_content}" | openssl dgst -sha256 -hmac "${SHARED_KEY}")
  TOKEN="NHSMESH ${MAILBOX}:${nonce}:${nonce_count}:${timestamp}:${hash_value}"
}

authorization() {
  curl -i ${CURL_FLAGS} -X POST "https://${HOST}/messageexchange/${MAILBOX}" --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H 'Mex-ClientVersion: 1.0' -H 'Mex-OSVersion: 1.0' -H 'Mex-OSName: MacOS' -H "Authorization: ${TOKEN}"
}

inbox() {
  curl -i ${CURL_FLAGS} -X GET "https://${HOST}/messageexchange/${MAILBOX}/inbox" --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H 'Mex-ClientVersion: 1.0' -H 'Mex-OSVersion: 1.0' -H 'Mex-OSName: MacOS' -H "Authorization: ${TOKEN}"
}

send() {
  local body
  body="$1"
  curl -i ${CURL_FLAGS} -X POST "https://${HOST}/messageexchange/${MAILBOX}/outbox" --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H 'Mex-ClientVersion: 1.0' -H 'Mex-OSVersion: 1.0' -H 'Mex-OSName: MacOS' -H 'Content-Type:application/octet-stream' -H "Mex-From: ${MAILBOX}" -H "Mex-To: ${TO_MAILBOX}" -H 'Mex-MessageType: DATA' -H 'Mex-WorkflowID: workflow1' -H 'Mex-FileName: test-filename.txt' -H 'Mex-Version: 1.0' -H "Authorization: ${TOKEN}" -d "${body}"
}

download() {
  local message_id
  message_id=$1
  curl -i ${CURL_FLAGS} -X GET "https://${HOST}/messageexchange/${MAILBOX}/inbox/${message_id}" --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H 'Mex-ClientVersion: 1.0' -H 'Mex-OSVersion: 1.0' -H 'Mex-OSName: MacOS' -H "Authorization: ${TOKEN}"
}

ack() {
  local message_id
  message_id=$1
  curl -i ${CURL_FLAGS} -X PUT "https://${HOST}/messageexchange/${MAILBOX}/inbox/${message_id}/status/acknowledged" --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H 'Mex-ClientVersion: 1.0' -H 'Mex-OSVersion: 1.0' -H 'Mex-OSName: MacOS' -H "Authorization: ${TOKEN}"
}

create_token

if [ "$1" = "auth" ]
then
  authorization
elif [ "$1" = "inbox" ]
then
  inbox
elif [ "$1" = "send" ]
then
  send "$2"
elif [ "$1" = "download" ]
then
  download "$2"
elif [ "$1" = "ack" ]
then
  ack "$2"
fi
