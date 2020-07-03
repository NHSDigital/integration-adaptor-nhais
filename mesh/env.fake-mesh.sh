# To use any of the optional configuration options uncomment that line. If you no longer want to use an optional
# configuration option then comment that line. Don't use empty string '' or you'll likely to break things

# Your MESH mailbox id (sender)
export MAILBOX_ID='fake_mailboxId'

# Default fake-mesh password
export MAILBOX_PASSWORD='password'

# (optional) mailbox is to send messages to (recipient). If not provided all messages are sent to MAILBOX_ID
#export TO_MAILBOX=''

# Shared key used to generate auth token.
export SHARED_KEY='SharedKey'

# hostname and port (not scheme) of the MESH API.
export HOST='localhost:8829'

# path to the file containing the fake-mesh private key.
export OPENTEST_ENDPOINT_PRIVATE_KEY="./fakemesh.ca.key.pem"

# path the file containg the fake-mesh endpoint certificate.
export OPENTEST_ENDPOINT_CERT="./fakemesh.ca.cert.pem"

# (optional) provide different flags / options for the curl command
#export CURL_FLAGS="-s -i -k -o /dev/null"