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
      "valueString": "3"
    },
    {
      "name": "acceptanceDate",
      "valueString": "1992-01-15"
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
             "value": "4826940,281"
           }
          }
        ],
        "name": [
          {
            "family": "ASQUITH",
            "given": [
              "ROGER"
            ],
            "prefix": [
              "LORD"
            ]
          }
        ],
        "gender": "male",
        "birthDate": "1946-07-23",
        "address": [
          {
            "line": [
              "THE MANSION HOUSE",
              "",
              "ST PAULS CRAY",
              "ORPINGTON",
              "KENT"
            ],
            "postalCode": "BR12 7ET",
            "use": "home"
          },
          {
            "line": [
                "",
                "",
                "",
                "HORSFORTH",
                "LEEDS"
            ],
            "use": "old"
          }
        ],
        "extension": [
          {
            "url": "http://hl7.org/fhir/StructureDefinition/patient-birthPlace",
            "valueString": "PETERBOROUGH"
          },
          {
            "url": "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-PreviousGP",
            "valueString": "DR BLACK"
          },
        ],
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}