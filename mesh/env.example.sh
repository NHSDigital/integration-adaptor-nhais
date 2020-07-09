# To use any of the optional configuration options uncomment that line. If you no longer want to use an optional
# configuration option then comment that line. Don't use empty string '' or you'll likely to break things

# The password for MAILBOX_ID can be found in your OpenTest welcome e-mail
export MAILBOX_PASSWORD=''

# (optional) mailbox is to send messages to (recipient). If not provided all messages are sent to MAILBOX_ID
#export TO_MAILBOX=''

# Shared key used to generate auth token. Provided by MESH operator (OpenTest, PTL, etc)
export SHARED_KEY=''

# (optional) hostname and port (not scheme) of the MESH API. default: OpenTest
#export HOST=''

# (optional) path to the file containing the opentest private key. default: ~/opentest.private.key
#export OPENTEST_ENDPOINT_PRIVATE_KEY=''

# (optional) path the file containg the opentest endpoint certificate. default: ~/opentest.endpoint.cert
#export OPENTEST_ENDPOINT_CERT=''

# (optional) provide different flags / options for the curl command
#export CURL_FLAGS="-s -i -k -o /dev/null"

# WorkflowID used to send MESH messages. Can be either NHAIS_REG or NHAIS_RECEP
export WORKFLOW_ID=NHAIS_REG