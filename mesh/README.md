# MESH Command Line Tool

A command line tool to use with [NHS Digital's MESH API](https://meshapi.docs.apiary.io/) for testing purposes.

## Setup

Copy `env.example.sh` to `env.sh` and fill in the values as described in the comments and below.

You **MUST** provide files containing **your** OpenTest endpoint private key and endpoint certificate.
These can be found in your OpenTest welcome email. Copy the values into either the path/files used by default
(see `env.example.sh` for instruction) or to paths/files of your choosing (provided you set the variables
accordingly)

## Usage

### Authenticate

MESH API docs says to do this first but our experience seems that its not needed.

    ./mesh.sh auth
    
### List Inbox Messages

Performs the "Check inbox" operation. The JSON response is printed to the console.

    ./mesh.sh inbox
    
### Send a message

Send a message with message content provided on the command line

    ./mesh.sh send "my message content"
    
Send a message with message content provided by a file

    ./mesh.sh send "@../src/intTest/resources/inbound_uat_data/approval/app-j-with-nhs-number.edifact.dat"
    
### Download a message

Download a message. Message content is printed to the console. The message id `20200603145356720373_0D25C7` 
is used in this example. Get the message id from the response of the `inbox` or `send` commands.

    ./mesh.sh download 20200603145356720373_0D25C7

### Acknowledge a message

Acknowledge a message to remove it from your inbox. The message id `20200603145356720373_0D25C7` 
is used in this example. Get the message id from the response of the `inbox` or `send` commands.

    ./mesh.sh ack 20200603145356720373_0D25C7

## Using with Fake MESH

Copy `env.fake-mesh.sh` to `env.sh`
    
Note the value of MAILBOX_ID. This should be changed for the scenario being tested to reflect the mailbox id that the 
application uses to send or receive messages.

### Benchmarking

Silence curl

    CURL_FLAGS="-s -i -k -o /dev/null"

Use xargs to run in parallel

     $ time echo {0..1000} | xargs -n 1 -P 4 ./mesh.sh send @GPHA_SAMPLE.dat
       echo {0..1000}  0.00s user 0.00s system 63% cpu 0.001 total
       xargs -n 1 -P 4 ./mesh.sh send @GPHA_SAMPLE.dat  35.40s user 18.87s system 164% cpu 32.905 total

Shows 1000 / 32.905 ~= 30 writes per second using 4 parallel processes. Note that the fake-mesh database 
(LMDB) does not support parallel writes.

Setting the option lock=False in the database driver doubles throughput, but I'm unsure if this is a good idea.