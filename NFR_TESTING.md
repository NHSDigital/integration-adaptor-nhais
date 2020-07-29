# Testing non-functional requirements

## V & P Test Tools

### RECEP Responder

The source folder `src/recepResponder` contains a fake responder that produces RECEP messages. This is effectively a 
version of the NHAIS adaptor with registration message handling disabled.

#### Configuration

The RECEP Responder uses all of the same environment variable names as the adaptor. It is important that some of the
values of these variables are different for the RECEP responder to prevent it from interfering with the adaptor.

* The queue names `NHAIS_MESH_OUTBOUND_QUEUE_NAME`, `NHAIS_MESH_INBOUND_QUEUE_NAME`, and 
`NHAIS_GP_SYSTEM_INBOUND_QUEUE_NAME` **MUST** be different from the values used by the NHAIS adaptor. Only 
`NHAIS_MESH_INBOUND_QUEUE_NAME` is used to process the RECEP messages.
* `NHAIS_MONGO_DATABASE_NAME` **MUST** be different from the value used by the NHAIS adaptor.
* `NHAIS_MESH_MAILBOX_ID` must be the ID of the mailbox that the adaptor sends outbound messages to
* `NHAIS_MESH_CYPHER_TO_MAILBOX` must use the mailbox ID where the adaptor receives inbound messages. There must be
a mapping for every GP Trading Partner code used in the tests.

The `src/recepResponder/resources/application.yml` contains defaults for testing locally.

#### Running

From your IDE run `src/recepResponder/java/uk/nhs/digital/nhsconnect/nhais/ResponderNhaisApplication.java`
