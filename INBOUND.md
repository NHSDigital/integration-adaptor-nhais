# Inbound (HA->GP) Message Formats

## Queue

The name of the inbound GP system message queue is configurable using an environment variable (see [README](./README.md)).
All inbound (HA->GP) messages are published to this same queue. The message types can be differentiated using a header.

## Message Header

| Name            | Description 
|-----------------|---
| OperationId     | Unique identifier for the message. If the message is a reply to the previous outbound transaction then this id will match the OperationId returned by that outbound request.
| TransactionType | The type of transaction represented by the message. See 'Supported Transaction Types' below.

### Supported Transaction Types

| TransactionType Value | Description
|-----------------------|---
| `approval`            | Approval transaction
| `rejection`           | Rejection (Wrong HA) transaction

## Message Body

The body of every message is an [HL7 FHIR R4 Parameters](https://www.hl7.org/fhir/parameters.html). There is always
be one resource parameters named `patient` containing a [Patient](https://www.hl7.org/fhir/patient.html) resource. 
There are additional parameters consisting of name/value pairs.

## Approval Transaction

| Data Item                | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|--------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| GP Trading Partner Code  | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| Patient's Responsible GP | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| Patient's Responsible HA | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| NHS Number               | Patient       | /identifier/0/value                     |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"                                           |

## Rejection (Wrong HA) transaction

| Data Item                | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|--------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| GP Trading Partner Code  | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| Patient's Responsible GP | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| Patient's Responsible HA | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| NHS Number               | Patient       | /identifier/0/value                     |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"                                           |
| Rejection Details        | Parameter     | freeText                                | valueString              |                                    |                                                                                         |
