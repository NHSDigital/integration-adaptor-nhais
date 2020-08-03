# Inbound EDIFACT interchange generator

A Python scripts that generate and send inbound EDIFACT interchanges.

##Generating test data - inbound-generator.py

Example:
````
python3 inbound-generator.py --count 1750
````

Number of generated files depends on  `--count` parameter - default and minimum is 1 maximum 9999
(for V&P testing it is recommended to use at least 1750 files)

Script creates files in `edifact-generator/output/` folder.

All generated files have exactly the same messages/transactions. 
Only header and footer of EDIFACT interchange generate dynamically.

Each interchange contains all supported by NHAIS adaptor inbound transaction types.

##Sending test data - sender.py
Example:
````
python3 sender.py --mailbox gp_mailbox --limit 1750
````

This script sends all (or up to `limit`) EDIFACT messages from [./output/](./output) folder to defined MESH mailbox.

This script uses shell scripts located in [\<project_root\>/mesh](../mesh) folder to send EDIFACT messages.
It uses the same connection configuration as [\<project_root\>/mesh/mesh.sh](../mesh/mesh.sh) uses.
See [\<project_root\>/mesh/README.md](../mesh/README.md) for details.

Sender script can be configured using arguments:
 - mailbox - name of the mailbox that messages will be sent to (default: gp_mailbox)
 - limit - maximum number of messages that will be sent (default: 9999)
 
Before each use the state databases of NHAIS adapter should be cleared as messages could be treated as duplicates.