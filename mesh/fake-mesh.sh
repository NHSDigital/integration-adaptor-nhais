#!/usr/bin/env bash

MAILBOX_PASSWORD='password'
SHARED_KEY='SharedKey'
MAILBOX_ID='fake_mailboxId'
TOKEN=''
HOST='localhost:8829'
OPENTEST_ENDPOINT_PRIVATE_KEY="./fakemesh.ca.key.pem"
OPENTEST_ENDPOINT_CERT="./fakemesh.ca.cert.pem"
CURL_FLAGS="${CURL_FLAGS:--i -k}" # insecure, disable cert validation for fake-mesh. Add -i for headers

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
  hash_value=$(echo -n "${hash_content}" | openssl dgst -sha256 -hmac "${SHARED_KEY}")
  TOKEN="NHSMESH ${MAILBOX_ID}:${nonce}:${nonce_count}:${timestamp}:${hash_value}"
}

authorization() {
  curl ${CURL_FLAGS} -X POST "https://${HOST}/messageexchange/${MAILBOX_ID}" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
}

inbox() {
  echo ${CURL_FLAGS} -X GET "https://${HOST}/messageexchange/${MAILBOX_ID}/inbox" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
  curl ${CURL_FLAGS} -X GET "https://${HOST}/messageexchange/${MAILBOX_ID}/inbox" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" \
    -H 'Mex-ClientVersion: 1.0' -H 'Mex-JavaVersion: 1.7.0_60' -H 'Mex-OSArchitecture: Windows 7' -H 'Mex-OSName: x86' -H 'Mex-OSVersion: 6.1'
}

send() {
  local body
  body="$1"
  curl ${CURL_FLAGS} -X POST "https://${HOST}/messageexchange/${MAILBOX_ID}/outbox" \
    --cert "${OPENTEST_ENDPOINT_CERT}" --key "${OPENTEST_ENDPOINT_PRIVATE_KEY}" -H "Authorization: ${TOKEN}" -d "${body}" \
    -H "Mex-From: ${MAILBOX_ID}" -H "Mex-To: ${TO_MAILBOX}" -H 'Mex-WorkflowID: workflow1' \
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
  send "$2"
elif [ "$1" = "download" ]
then
  download "$2"
elif [ "$1" = "ack" ]
then
  ack "$2"
fi
