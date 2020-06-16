var headers = {
    'TransactionType': 'rejection',
    // a rejection is always a reply to a previous outbound Acceptance and needs to include an OperationId matching the
    // OperationId from the Acceptance that solicited the reply
    'OperationId': 'abc234...'
}

var body = {
  "parameter": [
    {
      "name": "gpTradingPartnerCode",
      "valueString": "TES5"
    },
    {
      "name": "freeText",
      "valueString": "WRONG HA - TRY SURREY"
    }
    {
      "name": "patient",
      "resource": {
        "managingOrganization": {
          "identifier": [{
            "system": "https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation",
            "value": "XX1"
          }]
        },
        "generalPractitioner": [
          {
           "identifier": {
             "system": "https://fhir.hl7.org.uk/Id/gmc-number",
             "value": "4826940,281"
           }
          }
        ],
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}