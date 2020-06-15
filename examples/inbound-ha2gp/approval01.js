var headers = {
    'TransactionType': 'approval',
    // a rejection is always a reply to a previous outbound Acceptance and needs to include an OperationId matching the
    // OperationId from the Acceptance that solicited the reply
    'OperationId': 'abc234...'
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
          "reference": "XX1"
        },
        "generalPractitioner": [
          {
            "reference": "2750922,295"
          }
        ],
        "identifier": [
          {
            "system": "https://fhir.nhs.uk/Id/nhs-number",
            "value": "RAT56"
          }
        ]
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}