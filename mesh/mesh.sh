#!/usr/bin/env bash

#source env.sh #custom configuration (i.e. OpentTest)
source env.sh

MAILBOX_ID="$2"
TO_MAILBOX=${TO_MAILBOX:-${MAILBOX_ID}}
TOKEN=''
HOST="${HOST:-msg.opentest.hscic.gov.uk}"
# "Your endpoint private key" from your OpenTest registration e-mail
OPENTEST_ENDPOINT_PRIVATE_KEY="${OPENTEST_ENDPOINT_PRIVATE_KEY:-${HOME}/opentest.private.key}"
# "Your endpoint certificate" from your OpenTest registration e-mail
OPENTEST_ENDPOINT_CERT="${OPENTEST_ENDPOINT_CERT:-${HOME}/opentest.endpoint.cert}"
CURL_FLAGS="${CURL_FLAGS:--i -k}" # insecure, disable cert validation for fake-mesh. Add -i for headers
WORKFLOW_ID="${WORKFLOW_ID:NHAIS_REG}"

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
  hash_content="${MAILBOX_ID}:${nonce}:${nonce_count}:${MAILBOX_PASSWORD}:${timestamp}"
  hash_value=$(echo -n "${hash_content}" | openssl dgst -sha256 -hmac "${SHARED_KEY}" | sed 's/^.* //')
  TOKEN="NHSMESH ${MAILBOX_ID}:${nonce}:${nonce_count}:${timestamp}:${hash_value}"
}

authorization() {
  curl ${CURL_FLAGS} -X POST "https://${HOST}/messageexchange/${MAILBOX_ID}" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
}

inbox() {
  echo ${OPENTEST_ENDPOINT_CERT}
  echo ${OPENTEST_ENDPOINT_PRIVATE_KEY}
  echo ${MAILBOX_ID}
  echo ${HOST}
  echo ${TOKEN}
  echo https://${HOST}/messageexchange/${MAILBOX_ID}/inbox
  curl ${CURL_FLAGS} -X GET "https://${HOST}/messageexchange/${MAILBOX_ID}/inbox" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
}

send() {
  local body
  body="$1"
  curl ${CURL_FLAGS} -X POST "https://${HOST}/messageexchange/${MAILBOX_ID}/outbox" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" -d "${body}" \
    -H "Mex-From: ${MAILBOX_ID}" -H "Mex-To: ${TO_MAILBOX}" -H "Mex-WorkflowID: ${WORKFLOW_ID}" \
    -H 'Content-Type:application/octet-stream' -H 'Mex-MessageType: DATA'  -H 'Mex-FileName: test-filename.txt' -H 'Mex-Version: 1.0' \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
}

download() {
  local message_id
  message_id=$1
  curl ${CURL_FLAGS} -X GET "https://${HOST}/messageexchange/${MAILBOX_ID}/inbox/${message_id}" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
}

ack() {
  local message_id
  message_id=$1
  curl -i ${CURL_FLAGS} -X PUT "https://${HOST}/messageexchange/${MAILBOX_ID}/inbox/${message_id}/status/acknowledged" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
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
  send "$3"
elif [ "$1" = "download" ]
then
  download "$3"
elif [ "$1" = "ack" ]
then
  ack "$3"
fi
