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
          "reference": "XX1"
        },
        "generalPractitioner": [
          {
            "reference": "4826940,281"
          }
        ],
        "identifier": [
          {
            "system": "https://fhir.nhs.uk/Id/nhs-number",
            "value": "T247"
          }
        ]
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}