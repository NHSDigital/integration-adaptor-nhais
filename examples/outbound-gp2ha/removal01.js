{
  "parameter": [
    {
      "name": "gpTradingPartnerCode",
      "valueString": "TES5"
    },
    {
      "name": "freeText",
      "valueString": "PATIENT NOW LIVES 24 MILES FROM PRACTICE"
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