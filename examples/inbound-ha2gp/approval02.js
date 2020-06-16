var headers = {
    'TransactionType': 'approval',
    // an approval is always a reply to a previous outbound Acceptance and needs to include an OperationId matching the
    // OperationId from the Acceptance that solicited the reply
    // OperationId should be treated as case-insensitive
    'OperationId': '6BA3016176936E006FF0B81D5AFE3AA18B701AA6A51712ED784FFF7678BC32EA'
}

var body = {
  "parameter": [
    {
      "name": "gpTradingPartnerCode",
      "valueString": "TES5"
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
             "value": "2750922,295"
           }
          }
        ],
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}