# PatientsApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**acceptance**](PatientsApi.md#acceptance) | **POST** /Patient/{id} | Accept a new patient (Acceptance transaction)
[**amendment**](PatientsApi.md#amendment) | **PATCH** /Patient/{id} | Amend patient details (Amendment transaction)
[**deduction**](PatientsApi.md#deduction) | **POST** /Patient/{id}/$nhais.deduction | Deduct a patient (Deduction transaction)
[**removal**](PatientsApi.md#removal) | **POST** /Patient/{id}/$nhais.removal | Accept a new patient (Acceptance transaction)


<a name="acceptance"></a>
# **acceptance**
> OperationOutcome acceptance(id, patient, acceptanceType)

Accept a new patient (Acceptance transaction)

    ## Overview Use this endpoint to send an Acceptance to NHAIS.  ## Supported acceptance types and fields  Refer to the examples to see how the fields are represented in the request body.  The following is paraphrased from:  HA/GP LINKS - REGISTRATION, GP SYSTEMS SPECIFICATION  3.3.3.a  Algorithm to determine the Patient Acceptance Type  https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation  (*) &#x3D; Mandatory field  [Deprecated] &#x3D; fields described in the original NHAIS specification that have since been deprecated and are not supported by the adaptor  It is anticipated that the GP System will prompt the GP to determine the following minimum basic information about the patient:  * NHS Number * Surname (*) * Previous Surname * Forename(s) * Title * Sex (*) *  Date of Birth * Present Address excluding Postcode (*) * Postcode * Place of Birth [mandatory if NHS Number not entered] * Patient&#39;s Responsible GP (*) * Patient&#39;s Responsible HA (*)  The supported and required fields are each type of acceptance are as follows.  ### Birth  * Patient&#39;s Responsible GP (*) * Patient&#39;s Responsible HA (*) * NHS Number (*) * Surname (*) * Previous Surname * First Forename * Second Forename * Other Forenames * Title * Sex (*) * Date of Birth (*) * Present Address excluding Postcode (*) * Postcode * Place of Birth * Drugs Dispensed Marker * [Deprecated] RPP Mileage * [Deprecated] Blocked Route/Special District Marker * [Deprecated] Walking Units * Residential Institute Code * Free Text (GP Notes)  ### First Acceptance  * Patient&#39;s Responsible GP (*) * Patient&#39;s Responsible HA (*) * NHS Number * Surname (*) * Previous Surname * First Forename * Second Forename * Other Forenames * Title * Sex (*) * Date of Birth * Present Address excluding Postcode (*) * Postcode * Place of Birth [mandatory if NHS Number not entered] *  Drugs Dispensed Marker * [Deprecated] RPP Mileage * [Deprecated] Blocked Route/Special District Marker * [Deprecated] Walking Units * Residential Institute Code * Free Text (GP Notes)  ### Transfer In  For Previous Address at least one of the five fields MUST be completed.  * Patient&#39;s Responsible GP (*) * Patient&#39;s Responsible HA (*) * NHS Number * Surname (*) * Previous Surname * First Forename * Second Forename * Other Forenames * Title * Sex (*) * Date of Birth * Present Address excluding Postcode (*) * Postcode * Place of Birth [mandatory if NHS Number not entered] * Drugs Dispensed Marker * [Deprecated] RPP Mileage * [Deprecated] Blocked Route/Special District Marker * [Deprecated] Walking Units * Residential Institute Code * Previous Address - 5 fields (*) * Previous GP Name (*) * Previous HA Cipher * Free Text (GP Notes)  ### Immigrant  * Patient&#39;s Responsible GP (*) * Patient&#39;s Responsible HA (*) * NHS Number * Surname (*) * Previous Surname * First Forename * Second Forename * Other Forenames * Title * Sex (*) * Date of Birth * Present Address excluding Postcode (*) * Postcode * Place of Birth [mandatory if NHS Number not entered] * Drugs Dispensed Marker * [Deprecated] RPP Mileage * [Deprecated] Blocked Route/Special District Marker * [Deprecated] Walking Units * Residential Institute Code * Date of Entry into the UK (*) * Date Patient left the UK * Previous Address - 5 fields * Previous GP Name * Previous HA Cipher * Free Text (GP Notes)  ### Ex-Services  * Patient&#39;s Responsible GP (*) * Patient&#39;s Responsible HA (*) * NHS Number * Surname (*) * Previous Surname * First Forename * Second Forename * Other Forenames * Title * Sex (*) * Date of Birth * Present Address excluding Postcode (*) * Postcode * Place of Birth [mandatory if NHS Number not entered] * Drugs Dispensed Marker * [Deprecated] RPP Mileage * [Deprecated] Blocked Route/Special District Marker * [Deprecated] Walking Units * Residential Institute Code * Previous Address - 5 fields * Date of Enlistment (Joining) * Date of Enlistment (Leaving) * Free Text (GP Notes)  ## Operation Id When you submit an amendment an operation id will be included in the response. This operation id MUST be retained to match against the asynchronous reply from NHAIS  ## Sandbox test scenarios TODO: document interactions supported by fake mesh  | Scenario                            | Request                                                      | Response                                         | | ----------------------------------- | ------------------------------------------------------------ | ------------------------------------------------ | |                                     |                                                              |                                                  | 

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| The patient&#39;s NHS Number. The primary identifier of a patient, unique within NHS England and Wales. Always 10 digits and must be a [valid NHS Number](https://www.datadictionary.nhs.uk/data_dictionary/attributes/n/nhs/nhs_number_de.asp). | [default to null]
 **patient** | [**Patient**](..//Models/Patient.md)|  |
 **acceptanceType** | **String**|  | [optional] [default to null] [enum: birth, first, transfer, immigrant, exservices]

### Return type

[**OperationOutcome**](..//Models/OperationOutcome.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/fhir+json

<a name="amendment"></a>
# **amendment**
> OperationOutcome amendment(id, inlineObject)

Amend patient details (Amendment transaction)

    ## Overview Use this endpoint to send an Amendment to NHAIS.  This is a &#39;patch&#39; operation, meaning you can update specific parts of the patient record (such as a name or an address), as opposed to having to update the entire record.  ## Operation Id When you submit an amendment an operation id will be included in the response. This operation id MUST be retained to match against the asynchronous reply from NHAIS  ## EDIFACT  The following Interchange contains two Amendment transactions for:  NHS Number SEQ12. New Title : Mrs. New Surname : Patterson. New Previous Surname : Smythe. New Address : Holly Cottage, 12 Long Lane, Bromley, Kent, BR5 4ER. Registered with GP 295 (GMC National GP Code 2750922) - now a dispensing GP for this patient. New Address has an RPP Mileage of  7, a Special District classification, Walking Units of 6 and a Residential Institute Code of AA. The GP has added some free text (GP Notes) at the time of registration.  NHS Number 123/23/123. New Address : Flat 49, 23 Jackson Square, St Pauls Cray, Orpington, Kent. Registered with GP 281 (GMC National GP Code 4826940). New Address no longer has RPP Mileage, Blocked Route/Special District Classification or Walking Units.  Information transmitted within Interchange 7, Message 8, Transaction Numbers 22 and 23 at 15:29 on 16/01/1992.  | EDIFACT                                    | Notes                                                           | |--------------------------------------------|-----------------------------------------------------------------| | UNB+UNOA:2+TES5+XX11+920116:1529+00000007&#39; | SIS 00000007 generated by adaptor, abstracted by operation id, timestamp is when message was translated | UNH+00000008+FHSREG:0:1:FH:FHS001&#39;         | SMS 00000008 generated by adaptor, abstracted by operation id | BGM+++507&#39;                                 | n/a for API | NAD+FHS+XX1:954&#39;                           | XX1 is the cypher of the HA. Supplied by managingOrganization.identifier[0].value in the request body | DTM+137:199201171259:203&#39;                  | TODO: expectation is this &#x3D;&#x3D; timestamp in UNB but this example is different | RFF+950:G2&#39;                                | \&quot;G3\&quot; &#x3D; GP to FHSA amendment is used for all PATCH operations | S01+1&#39;                                     | n/a for API | RFF+TN:22&#39;                                 | Transaction id generated by adaptor, abstracted by operation id | NAD+GP+2750922,295:900&#39;                    | GMC Code: 4826940, Local Code: 281. Supplied by       TBD | NAD+RIC+AA:956&#39;                            | Residential Institute Code         TBD | QTY+951:7&#39; | QTY+952:6&#39; | HEA+BM+S:ZZZ&#39; | HEA+DM+Y:ZZZ&#39; | FTX+RGI+++NOW AT THE ARTHUR ANDREWS CENTRE&#39; | S02+2&#39; | PNA+PAT+SEQ12:OPI+++SU:PATTERSON++TI:MRS&#39; | NAD+PAT++HOLLY COTTAGE:12 LONG LANE::BROMLEY:KENT+++++BR5  4ER&#39; | S02+2&#39; | PNA+PER++++SU:SMYTHE&#39; | S01+1&#39; | RFF+TN:23&#39; | NAD+GP+4826940,281:900&#39; | QTY+951:%&#39; | QTY+952:%&#39; | HEA+BM+%:ZZZ&#39; | S02+2&#39; | PNA+PAT+123/23/123:OPI&#39; | NAD+PAT++FLAT 49:23 JACKSON SQUARE:ST PAULS CRAY:ORPINGTON:KENT&#39; | UNT+29+00000008&#39; | UNZ+1+00000007&#39;     ## Sandbox test scenarios TODO: document interactions supported by fake mesh  | Scenario                            | Request                                                      | Response                                         | | ----------------------------------- | ------------------------------------------------------------ | ------------------------------------------------ | |                                     |                                                              |                                                  | 

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| The patient&#39;s NHS Number. The primary identifier of a patient, unique within NHS England and Wales. Always 10 digits and must be a [valid NHS Number](https://www.datadictionary.nhs.uk/data_dictionary/attributes/n/nhs/nhs_number_de.asp). | [default to null]
 **inlineObject** | [**InlineObject**](..//Models/InlineObject.md)|  |

### Return type

[**OperationOutcome**](..//Models/OperationOutcome.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/fhir+json

<a name="deduction"></a>
# **deduction**
> OperationOutcome deduction(id, patient)

Deduct a patient (Deduction transaction)

    ## Overview Use this endpoint to send a Deduction transaction to NHAIS.  ## Operation Id When you submit an amendment an operation id will be included in the response. This operation id MUST be retained to match against the asynchronous reply from NHAIS  ## EDIFACT  The following Interchange contains just one Deduction Request transaction for:  NHS Number N/10/10. Deduction Date : 25/12/1991, Deduction Reason : Death, Free Text : Died on holiday in Majorca. Registered with GP 281 (GMC National GP Code 4826940).  Information transmitted within Interchange 2, Message 3, Transaction Number 17 at 13:17 on 13/01/1992.  | EDIFACT                                    | Notes                                                           | |--------------------------------------------|-----------------------------------------------------------------| | UNB+UNOA:2+TES5+XX11+920113:1317+00000002&#39; | SIS 00000002 generated by adaptor, abstracted by operation id, timestamp must match DTM | | UNH+00000003+FHSREG:0:1:FH:FHS001&#39;         | SMS 00000003 generated by adaptor, abstracted by operation id   | | BGM+++507&#39;                                 | n/a for API                                                     | | NAD+FHS+XX1:954&#39;                           | XX1 is the cypher of the HA. Supplied by managingOrganization.identifier[0].value in the request body | | DTM+137:199201131317:203&#39;                  | Timestamp of translation, adaptor will generate this            | | RFF+950:G5&#39;                                | The transaction type G5 is used for all $nhais.deduction operations | | S01+1&#39;                                     | n/a for API                                                     | | RFF+TN:17&#39;                                 | Transaction id generated by adaptor, abstracted by operation id | | NAD+GP+4826940,281:900&#39;                    | GMC Code: 4826940, Local Code: 281. Supplied by generalPractitioner[].value in the request body. | | GIS+1:ZZZ&#39;                                 | Can be: 1 - Death, 5 - Emigrated, 13 - Other reason        TODO | | DTM+961:19911225:102&#39;                      | 961 indicates this is a date of deduction                  TODO | | FTX+RGI+++DIED ON HOLIDAY IN MAJORCA&#39;      | Free text deduction reason                                 TODO | | S02+2&#39;                                     | n/a for API                                                     | | PNA+PAT+N/10/10:OPI&#39;                       | N/10/10 is the NHS Number. Suppied by identifier[0].value (see examples) | | UNT+14+00000003&#39;                           | n/a for API                                                     | | UNZ+1+00000002&#39;                            | n/a for API                                                     |  *TODO*: No UK Core representation of an HA, we need to define this ourselves *TODO*: No UK Core representations of GMC or local code, we need to define this ourselves *TODO*: Create a deductionDetails extension for patient  ## Sandbox test scenarios *TODO*: document interactions supported by fake mesh  | Scenario                            | Request                                                      | Response                                         | | ----------------------------------- | ------------------------------------------------------------ | ------------------------------------------------ | |                                     |                                                              |                                                  | 

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| The patient&#39;s NHS Number. The primary identifier of a patient, unique within NHS England and Wales. Always 10 digits and must be a [valid NHS Number](https://www.datadictionary.nhs.uk/data_dictionary/attributes/n/nhs/nhs_number_de.asp). | [default to null]
 **patient** | [**Patient**](..//Models/Patient.md)|  |

### Return type

[**OperationOutcome**](..//Models/OperationOutcome.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/fhir+json

<a name="removal"></a>
# **removal**
> OperationOutcome removal(id, patient)

Accept a new patient (Acceptance transaction)

    ## Overview Use this endpoint to send a Removal transaction to NHAIS.  ## Operation Id TODO: operation id (transaction id) will probably need to be provided in the request since this is a reply to a HA -&gt; GP amendment  ## EDIFACT  The following Interchange contains one Acceptance transaction - a Type 1 (Birth) Acceptance - one Amendment transaction and one Removal (Out of Area) transaction as follows:  Acceptance for Mr. Peter Martin Stevens, NHS Number ACE99A999. Born : 07/12/1991 in Bury. Address : Middle Farm, New Street, St Pauls Cray, Orpington, Kent, BR1 5ER. Registered with GP 281 (GMC National GP Code 4826940).  Amendment for NHS Number ABCDE1234. New Address : Flat 1a Spencer House, Card Road, St Pauls Cray, Orpington, Kent. Registered with GP 281 (GMC National GP Code 4826940).  Removal (Out of Area) for NHS Number T247. Reason for Removal : PATIENT NOW LIVES 24 MILES FROM PRACTICE Registered with GP 281 (GMC National GP Code 4826940).  Information transmitted within Interchange 8, Messages 9 to 11, Transaction Numbers 24 to 26 at 10:21 on 17/01/1992.  | EDIFACT                                    | Notes                                                           | |--------------------------------------------|-----------------------------------------------------------------| | UNB+UNOA:2+TES5+XX11+920117:1021+00000008&#39; | SIS 00000002 generated by adaptor, abstracted by operation id, timestamp must match DTM | UNH+00000011+FHSREG:0:1:FH:FHS001&#39;         | SMS 00000003 generated by adaptor, abstracted by operation id | BGM+++507&#39;                                 | n/a for API | NAD+FHS+XX1:954&#39;                           | XX1 is the cypher of the HA. Supplied by managingOrganization.identifier[0].value in the request body | DTM+137:199201171259:203&#39;                  | TODO: expectation is this &#x3D;&#x3D; timestamp in UNB but this example is different | RFF+950:G3&#39;                                | \&quot;G3\&quot; &#x3D; GP to FHSA removal is used for all $nhais.removal operations | S01+1&#39;                                     | n/a for API | RFF+TN:26&#39;                                 | Transaction id generated by adaptor, abstracted by operation id | | NAD+GP+4826940,281:900&#39;                    | GMC Code: 4826940, Local Code: 281. Supplied by generalPractitioner[].value in the request body. | | FTX+RGI+++PATIENT NOW LIVES 24 MILES FROM PRACTICE&#39; | Free text deduction reason                                 TODO | | S02+2&#39;                                     | n/a for API | PNA+PAT+T247:OPI&#39;                          | T247 is the NHS Number. Suppied by identifier[0].value (see examples) | UNT+12+00000011&#39;                           | n/a for API | UNZ+3+00000008&#39;                            | n/a for API   ## Sandbox test scenarios TODO: document interactions supported by fake mesh  | Scenario                            | Request                                                      | Response                                         | | ----------------------------------- | ------------------------------------------------------------ | ------------------------------------------------ | |                                     |                                                              |                                                  | 

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| The patient&#39;s NHS Number. The primary identifier of a patient, unique within NHS England and Wales. Always 10 digits and must be a [valid NHS Number](https://www.datadictionary.nhs.uk/data_dictionary/attributes/n/nhs/nhs_number_de.asp). | [default to null]
 **patient** | [**Patient**](..//Models/Patient.md)|  |

### Return type

[**OperationOutcome**](..//Models/OperationOutcome.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/fhir+json

