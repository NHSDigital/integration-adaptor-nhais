{
  "parameter": [
    {
      "name": "gpTradingPartnerCode",
      "valueString": "TES5"
    },
    {
      "name": "acceptanceCode",
      "valueString": "A"
    },
    {
      "name": "acceptanceType",
      "valueString": "4"
    },
    {
      "name": "acceptanceDate",
      "valueString": "1992-01-15"
    },
    {
      "name": "dateOfUkEntry",
      "valueString": "1991-08-06"
    },
    {
      "name": "dateOfUkExit",
      "valueString": "1968-03-05"
    },
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
        "name": [
          {
            "family": "HOWES",
            "given": [
              "ALISON",
              "J"
            ],
            "prefix": [
              "MRS"
            ]
          }
        ],
        "gender": "female",
        "birthDate": "1965-12-12",
        "address": [
          {
            "line": [
              "",
              "13 FOX CRESCENT",
              "",
              "BROMLEY",
              "KENT"
            ],
            "postalCode": "BR1  7TQ",
            "use": "home"
          }
        ],
        "extension": [
          {
            "url": "http://hl7.org/fhir/StructureDefinition/patient-birthPlace",
            "valueString": "LANCASHIRE"
          }
        ],
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}