# MESH Command Line Tool

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

    ./mesh.sh send "@GPHA_SAMPLE.dat"
    
### Download a message

Download a message. Message content is printed to the console. The message id `20200603145356720373_0D25C7` 
is used in this example. Get the message id from the response of the `inbox` or `send` commands.

    ./mesh.sh download 20200603145356720373_0D25C7

### Acknowledge a message

Acknowledge a message to remove it from your inbox. The message id `20200603145356720373_0D25C7` 
is used in this example. Get the message id from the response of the `inbox` or `send` commands.

    ./mesh.sh ack 20200603145356720373_0D25C7
