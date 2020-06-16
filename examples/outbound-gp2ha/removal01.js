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