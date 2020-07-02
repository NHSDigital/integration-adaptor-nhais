# Inbound (HA->GP) Message Formats

## Queue

The name of the inbound GP system message queue is configurable using an environment variable (see [README](./README.md)).
All inbound (HA->GP) messages are published to this same queue. The message types can be differentiated using a header.

## Message Header

| Name            | Description 
|-----------------|---
| OperationId     | Unique identifier for the message. If the message is a reply to the previous outbound transaction then this id will match the OperationId returned by that outbound request.
| TransactionType | The type of transaction represented by the message. See 'Supported Transaction Types' below.

### Transaction Types

| Transaction Type        | Status              | Data Type | Description                 |
|-------------------------|---------------------|-----------|-----------------------------|
| approval                | Implemented         | FHIR      | Approval                    |
| rejection               | Implemented         | FHIR      | Rejection (Wrong HA)        |
| deduction               | Not Yet Implemented | FHIR      | Deduction                   |
| deduction_rejection     | Not Yet Implemented | FHIR      | Deduction Request Rejection |
| fp69_prior_notification | Not Yet Implemented | FHIR      | FP69 Prior Notification     |
| fp69_flag_removal       | Not Yet Implemented | FHIR      | FP69 Flag Removal           |
| amendment               | Not Yet Implemented | JSONPatch | Amendment                   |

## Messages with FHIR Data Type

The body of every FHIR message is an [HL7 FHIR R4 Parameters](https://www.hl7.org/fhir/parameters.html). There is always
one resource parameter named `patient` containing a [Patient](https://www.hl7.org/fhir/patient.html) resource. There 
are additional parameters consisting of name/value pairs.

### Data Items for FHIR Message Types

REQUIRED - NHAIS will always provide this value for the transaction and the adaptor will always translated it FHIR
OPTIONAL - NHAIS may optionally provide this value for the transaction and the adaptor will translate it to FHIR if provided
BLANK - the value is not used by this transaction type

| Data Item                                    | Approval | Rejection | FP69 Prior Notification |
|----------------------------------------------|----------|-----------|-------------------------|
| GP Trading Partner Code                      | REQUIRED | REQUIRED  | REQUIRED                |
| Patient's Responsible GP (GP Code)           | REQUIRED | REQUIRED  | REQUIRED                |
| Patient's Responsible HA (Sending HA Cipher) | REQUIRED | REQUIRED  | REQUIRED                |
| NHS Number                                   | OPTIONAL |           | REQUIRED                |
| Rejection Details (Free Text)                |          | OPTIONAL  |                         |
| Surname                                      |          |           | REQUIRED                |
| First Given Forename                         |          |           | REQUIRED                |
| Second Forename                              |          |           | OPTIONAL                |
| Other Forenames                              |          |           | OPTIONAL                |
| Date of Birth                                |          |           | REQUIRED                |
| Address - House Name                         |          |           | OPTIONAL                |
| Address - Number / Road Name                 |          |           | OPTIONAL                |
| Address - Locality                           |          |           | OPTIONAL                |
| Address - Post Town                          |          |           | REQUIRED                |
| Address - County                             |          |           | OPTIONAL                |
| Address - Postcode                           |          |           | OPTIONAL                |
| FP69 Expiry Date                             |          |           | REQUIRED                |
| FP69 Reason Code                             |          |           | REQUIRED                |
| HA Notes (Free Text)                         |          |           | OPTIONAL                |

### Approval

| Data Item                | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|--------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| GP Trading Partner Code  | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| Patient's Responsible GP | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| Patient's Responsible HA | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| NHS Number               | Patient       | /identifier/0/value                     |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"                                           |

### Rejection (Wrong HA)

| Data Item                | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|--------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| GP Trading Partner Code  | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| Patient's Responsible GP | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| Patient's Responsible HA | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| NHS Number               | Patient       | /identifier/0/value                     |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"                                           |
| Rejection Details        | Parameters    | freeText                                | valueString              |                                    |                                                                                         |

### FP69 Prior Notification

| Data Item                    | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|------------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| GP Trading Partner Code      | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| Patient's Responsible GP     | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| Patient's Responsible HA     | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| NHS Number                   | Patient       | /identifier/0/value                     |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"                                           |
| Surname                      | Patient       | /name/0/family                          |                          |                                    |                                                                                         |
| First Given Forename         | Patient       | /name/0/given/0                         |                          |                                    |                                                                                         |
| Second Forename              | Patient       | /name/0/given/1                         |                          |                                    |                                                                                         |
| Other Forenames              | Patient       | /name/0/given/2                         |                          |                                    |                                                                                         |
| Date of Birth                | Patient       | /birthDate                              |                          |                                    |                                                                                         |
| Address - House Name         | Patient       | /address/0/line/0                       |                          |                                    |                                                                                         |
| Address - Number / Road Name | Patient       | /address/0/line/1                       |                          |                                    |                                                                                         |
| Address - Locality           | Patient       | /address/0/line/2                       |                          |                                    |                                                                                         |
| Address - Post Town          | Patient       | /address/0/line/3                       |                          |                                    |                                                                                         |
| Address - County             | Patient       | /address/0/line/4                       |                          |                                    |                                                                                         |
| Address - Postcode           | Patient       | /address/0/postalCode                   |                          |                                    |                                                                                         |
| FP69 Expiry Date             | Parameters    | fp69ExpiryDate                          | valueString              | ISO 8601 Date                      |                                                                                         |
| FP69 Reason Code             | Parameters    | fp69ReasonCode                          | valueString              |                                    |                                                                                         |
| HA Notes (Free Text)         | Parameters    | freeText                                | valueString              |                                    |                                                                                         |

## Messages with JSONPatch Data Type

Amendment transaction use a JSONPatch data type instead of FHIR