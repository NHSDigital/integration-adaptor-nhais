# Inbound (HA->GP) Message Formats

## Queue

The name of the inbound GP system message queue is configurable using an environment variable (see [README](./README.md)).
The adaptor publishes all inbound (HA->GP) messages to this queue. A header value differentiates the types of transaction.

## Message Headers

| Header Name     | Description 
|-----------------|---
| OperationId     | The OperationId is generated from the GP or HA Trading Partner Code and the Transaction Number. 
| TransactionType | The type of transaction represented by the message. See 'Supported Transaction Types' below.

(*) The series of OperationId values for a Trading Partner Code repeats every 10,000,000 transactions. The GP MUST 
use the OperationId to match inbound Approvals and Rejections. The GP System SHOULD retain the OperationId for all 
other transactions. See "Transaction Matching" section below for more details.

### Transaction Types

| TransactionType Header Value | Data Type | Description
|------------------------------|-----------|---
| approval                     | FHIR      | Approval
| rejection                    | FHIR      | Rejection (Wrong HA)
| deduction                    | FHIR      | Deduction                   
| deduction_rejection          | FHIR      | Deduction Request Rejection
| fp69_prior_notification      | FHIR      | FP69 Prior Notification
| fp69_flag_removal            | FHIR      | FP69 Flag Removal
| amendment                    | JSONPatch | Amendment

Some transaction types are [obsolete](https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation) and 
not supported by the adaptor:

* MRF and MRS (Medical Records) transactions
* Close of quarter notification
* Reconciliation (upload/download) transactions

## Transaction Matching

The GP System Specification (Chapter 3) describes how to match, when applicable, inbound transactions to outbound 
transactions. GP Systems using the NHAIS Adaptor also follow this process with a single exception: For Approval and 
Rejection (Wrong HA) transactions where the specification states "GP System Transaction Number" the OperationId should 
be used instead.

## Messages with FHIR Data Type

The body of every FHIR message is an [HL7 FHIR R4 Parameters](https://www.hl7.org/fhir/parameters.html). There is always
one resource parameter named `patient` containing a [Patient](https://www.hl7.org/fhir/patient.html) resource. There 
are additional parameters consisting of name/value pairs.

### Data Items for FHIR Message Types

REQUIRED - NHAIS will always provide this value for the transaction and the adaptor will always translated it FHIR
OPTIONAL - NHAIS may optionally provide this value for the transaction and the adaptor will translate it to FHIR if provided
BLANK - the value is not used by this transaction type

| Data Item                                    | Approval | Rejection | FP69 Prior Notification | FP69 Flag Removal | Deduction | Deduction Request Rejection |
|----------------------------------------------|----------|-----------|-------------------------|-------------------|-----------|-----------------------------|
| GP Trading Partner Code                      | REQUIRED | REQUIRED  | REQUIRED                | REQUIRED          | REQUIRED  | REQUIRED                    |
| GP Code (Patient's Responsible GP)           | REQUIRED | REQUIRED  | REQUIRED                | REQUIRED          | REQUIRED  | REQUIRED                    |
| Sending HA Cipher (Patient's Responsible HA) | REQUIRED | REQUIRED  | REQUIRED                | REQUIRED          | REQUIRED  | REQUIRED                    |
| NHS Number                                   | OPTIONAL |           | REQUIRED                | REQUIRED          | REQUIRED  | REQUIRED                    |
| Rejection Details (Free Text)                |          | OPTIONAL  |                         |                   |           |                             |
| Surname                                      |          |           | REQUIRED                |                   |           |                             |
| First Given Forename                         |          |           | REQUIRED                |                   |           |                             |
| Second Forename                              |          |           | OPTIONAL                |                   |           |                             |
| Other Forenames                              |          |           | OPTIONAL                |                   |           |                             |
| Date of Birth                                |          |           | REQUIRED                |                   |           |                             |
| Address - House Name                         |          |           | OPTIONAL                |                   |           |                             |
| Address - Number / Road Name                 |          |           | OPTIONAL                |                   |           |                             |
| Address - Locality                           |          |           | OPTIONAL                |                   |           |                             |
| Address - Post Town                          |          |           | REQUIRED                |                   |           |                             |
| Address - County                             |          |           | OPTIONAL                |                   |           |                             |
| Address - Postcode                           |          |           | OPTIONAL                |                   |           |                             |
| FP69 Expiry Date                             |          |           | REQUIRED                |                   |           |                             |
| FP69 Reason Code                             |          |           | REQUIRED                |                   |           |                             |
| HA Notes (Free Text)                         |          |           | OPTIONAL                |                   |           | REQUIRED                    |
| Date of Deduction                            |          |           |                         |                   | REQUIRED  |                             |
| Reason for Deduction                         |          |           |                         |                   | REQUIRED  |                             |
| New HA Cipher                                |          |           |                         |                   | OPTIONAL  |                             |

### Approval

GP Links Specification Chapter 3.16.3 lists data fields and their requirements for in-coming approval transactions.


| Data Item                 | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|---------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| Transaction Type          | N/A           | N/A                                     | N/A                      | N/A                                | See "TransactionType" message header
| GP Trading Partner Code   | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| GP Code                   | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| Sending HA Cipher         | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| Transaction Date and Time | N/A           | N/A                                     | N/A                      | N/A                                | Managed by the adaptor
| Transaction Number        | N/A           | N/A                                     | N/A                      | N/A                                | Managed by the adaptor and abstracted by the OperationId message header
| NHS Number                | Patient       | /identifier/0/value                     |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"                                           |

### Rejection (Wrong HA)

GP Links Specification Chapter 3.15.4 lists data fields and their requirements for in-coming approval transactions.

| Data Item                 | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|---------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| Transaction Type          | N/A           | N/A                                     | N/A                      | N/A                                | See "TransactionType" message header
| GP Trading Partner Code   | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| GP Code                   | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| Sending HA Cipher         | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| Transaction Date and Time | N/A           | N/A                                     | N/A                      | N/A                                | Managed by the adaptor
| Transaction Number        | N/A           | N/A                                     | N/A                      | N/A                                | Managed by the adaptor and abstracted by the OperationId message header
| Rejection Details         | Parameters    | freeText                                | valueString              |                                    |                                                                                         |

### Deduction

GP Links Specification Chapter 3.14.4 lists data fields and their requirements for in-coming deduction transactions.

| Data Item                 | FHIR Resource | Patient JSON Pointer or Parameter Name   | Parameter Value Property | Format, if different from GP Links | Notes |
|---------------------------|---------------|------------------------------------------|--------------------------|------------------------------------|-------|
| Transaction Type          | N/A           | N/A                                      | N/A                      | N/A                                | See "TransactionType" message header
| GP Code                   | Patient       | /generalPractitioner/0/identifier/value  |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"
| GP Trading Partner Code   | Parameter     | gpTradingPartnerCode                     | valueString              |                                    |       |
| Sending HA Cipher         | Patient       | /managingOrganization/identifier/0/value |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation"
| Transaction Date and Time | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor
| Transaction Number        | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor and abstracted by the OperationId message header
| NHS Number                | Patient       | /identifier/0/value                      |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"
| Date of Deduction         | Parameter     | dateOfDeduction                          | valueString              |                                    |       |
| Reason for Deduction      | Parameter     | deductionReasonCode                      | valueString              |                                    | Refer to Chapter 3 / Appendix G for possible values
| New HA Cipher             | Parameter     | newHaCipher                              | valueString              |                                    |       |

### Deduction Request Rejection

GP Links Specification Chapter 3.23.4 lists data fields and their requirements for in-coming deduction request rejection transactions.

| Data Item                 | FHIR Resource | Patient JSON Pointer or Parameter Name   | Parameter Value Property | Format, if different from GP Links | Notes |
|---------------------------|---------------|------------------------------------------|--------------------------|------------------------------------|-------|
| Transaction Type          | N/A           | N/A                                      | N/A                      | N/A                                | See "TransactionType" message header
| GP Code                   | Patient       | /generalPractitioner/0/identifier/value  |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"
| GP Trading Partner Code   | Parameter     | gpTradingPartnerCode                     | valueString              |                                    |       |
| Sending HA Cipher         | Patient       | /managingOrganization/identifier/0/value |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation"
| Transaction Date and Time | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor
| Transaction Number        | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor and abstracted by the OperationId message header
| NHS Number                | Patient       | /identifier/0/value                      |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"
| Free Text                 | Parameter     | freeText                                 | valueString              |                                    |       |

### FP69 Prior Notification

GP Links Specification Chapter 3.23.4 lists data fields and their requirements for in-coming FP69 prior notification transactions.

| Data Item                    | FHIR Resource | Patient JSON Pointer or Parameter Name  | Parameter Value Property | Format, if different from GP Links | Notes                                                                                   |
|------------------------------|---------------|-----------------------------------------|--------------------------|------------------------------------|-----------------------------------------------------------------------------------------|
| Transaction Type             | N/A           | N/A                                      | N/A                      | N/A                                | See "TransactionType" message header
| GP Code                      | Patient       | /generalPractitioner/0/identifier/value |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"                                       |
| GP Trading Partner Code      | Parameters    | gpTradingPartnerCode                    | valueString              |                                    |                                                                                         |
| Sending HA Cipher            | Patient       | /managingOrganization/identifier/value  |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation" |
| Transaction Date and Time    | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor
| Transaction Number           | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor and abstracted by the OperationId message header
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
| Reason Code                  | Parameters    | fp69ReasonCode                          | valueString              |                                    |                                                                                         |
| HA Notes (Free Text)         | Parameters    | freeText                                | valueString              |                                    |                                                                                         |

### FP69 Flag Removal

GP Links Specification Chapter 3.22.4 lists data fields and their requirements for in-coming FP69 flag removal transactions.

| Data Item                 | FHIR Resource | Patient JSON Pointer or Parameter Name   | Parameter Value Property | Format, if different from GP Links | Notes |
|---------------------------|---------------|------------------------------------------|--------------------------|------------------------------------|-------|
| Transaction Type          | N/A           | N/A                                      | N/A                      | N/A                                | See "TransactionType" message header
| GP Code                   | Patient       | /generalPractitioner/0/identifier/value  |                          |                                    | "system": "https://fhir.hl7.org.uk/Id/gmc-number"
| GP Trading Partner Code   | Parameter     | gpTradingPartnerCode                     | valueString              |                                    |       |
| Sending HA Cipher         | Patient       | /managingOrganization/identifier/0/value |                          |                                    | "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation"
| Transaction Date and Time | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor
| Transaction Number        | N/A           | N/A                                      | N/A                      | N/A                                | Managed by the adaptor and abstracted by the OperationId message header
| NHS Number                | Patient       | /identifier/0/value                      |                          |                                    | "system": "https://fhir.nhs.uk/Id/nhs-number"

## Messages with JSONPatch Data Type

### Amendment

#### Data Items for Amendment

The inbound Amendment transaction uses a JSONPatch data type instead of FHIR. The format is very similar to outbound 
amendments with two exceptions:

* NHAIS may provide a new NHS number
* NHAIS never provides free text notes

| GP Links Data Item                        | Amendment  | Erasable |
|-------------------------------------------|------------|----------|
| Existing GP Code                          | REQUIRED   | n/a      |
| GP Trading Partner Code                   | REQUIRED   | n/a      |
| Sending HA Cipher                         | REQUIRED   | n/a      |
| Existing NHS Number                       | REQUIRED   | n/a      |
| Amended NHS Number                        | OPTIONAL   | NO       |
| New Surname                               | OPTIONAL   | NO       |
| New Previous Surname                      | OPTIONAL   | YES      |
| New First Forename                        | OPTIONAL   | YES (1)  |
| New Second Forename                       | OPTIONAL   | YES (1)  |
| New Other Forenames                       | OPTIONAL   | YES (1)  |
| New Title                                 | OPTIONAL   | YES      |
| New Sex                                   | OPTIONAL   | NO       |
| New Date of Birth                         | OPTIONAL   | NO       |
| New Address - House Name                  | OPTIONAL   | YES      |
| New Address - Number/Road Name            | OPTIONAL   | YES      |
| New Address - Locality                    | OPTIONAL   | YES      |
| New Address - Post Town                   | OPTIONAL   | NO       |
| New Address - County                      | OPTIONAL   | YES      |
| New Address - Postcode                    | OPTIONAL   | YES      |
| New Drugs Dispensed Marker                | OPTIONAL   | YES (2)  |
| New RPP Mileage                           | DEPRECATED | n/a      |
| New Blocked Route/Special District Marker | DEPRECATED | n/a      |
| New Walking Units                         | DEPRECATED | n/a      |
| New Residential Institute Code            | OPTIONAL   | YES (3)  |

(1) Forenames cannot be erased individually. The entire group of forenames will be erased with a remove operation on
the path `/name/0/given`.

(2) The Drugs Dispensed Marker is considered "erased" when the adaptor provides a replace operation for the entire 
extension with the "valueBoolean" value of false. The path will always be `/extension/0`.

(3) The Residential Institute Code is considered "erased" the adaptor provides a replace operation for the entire 
extension with "valueString" JSON value of null. The path will always be `/extension/0`.

#### Data Item Mappings for Amendment

| Data Item                             | Property Name        | JSONPatch "path" value                   | "value" format, if different from GP Links | Notes                                                               |
|---------------------------------------|----------------------|------------------------------------------|--------------------------------------------|---------------------------------------------------------------------|
| Existing GP Code                      | gpCode               |                                          |                                            |                                                                     |
| GP Trading Partner Code               | gpTradingPartnerCode |                                          |                                            |                                                                     |
| Destination HA Cipher                 | healthcarePartyCode  |                                          |                                            | Same value as the managing organisation identifier in an acceptance |
| NHS Number                            | nhsNumber            |                                          |                                            |                                                                     |
| Amended NHS Number                    |                      | /identifier/0/value                      |                                            |                                                                     |
| New Surname                           |                      | /name/0/family                           |                                            |                                                                     |
| New Previous Surname                  |                      | /name/1/family                           |                                            |                                                                     |
| New First Forename                    |                      | /name/0/given/0 For remove: name/0/given |                                            | If erased, all forenames will be erased as a group                  |
| New Second Forename                   |                      | /name/0/given/1 For remove: name/0/given |                                            | If erased, all forenames will be erased as a group                  |
| New Other Forenames                   |                      | /name/0/given/2 For remove: name/0/given |                                            | If erased, all forenames will be erased as a group                  |
| New Title                             |                      | /name/0/prefix/0                         |                                            |                                                                     |
| New Sex                               |                      | /gender                                  | male/female/unknown/other                  |                                                                     |
| New Date of Birth                     |                      | /birthDate                               | ISO 8601 Date                              |                                                                     |
| New Address - House Name              |                      | /address/0/line/0                        | (2)                                        |                                                                     |
| New Address - Number/Road Name        |                      | /address/0/line/1                        | (2)                                        |                                                                     |
| New Address - Locality                |                      | /address/0/line/2                        | (2)                                        |                                                                     |
| New Address - Post Town               |                      | /address/0/line/3                        |                                            |                                                                     |
| New Address - County                  |                      | /address/0/line/4                        | (2)                                        |                                                                     |
| New Address - Postcode                |                      | /address/0/postalCode                    |                                            |                                                                     |
| Drugs Dispensed Marker                |                      | /extension/0                             | (1)                                        | The value 'false' erases the Drugs Dispensed Marker                 |
| RPP Mileage                           | N/A                  |                                          |                                            |                                                                     |
| Blocked Route/Special District Marker | N/A                  |                                          |                                            |                                                                     |
| Walking Units                         | N/A                  |                                          |                                            |                                                                     |
| Residential Institute Code            |                      | /extension/0                             | (1)                                        | The value 'null' erases the Residential Institute Code              |

(1) The value will be the entire extension object and the path will always be /extension/0. Use the value of "url" to match the extension.

(2) Use JSON null for blank address lines. Either the "House Name" or the "Number/Road Name" MUST be present.


