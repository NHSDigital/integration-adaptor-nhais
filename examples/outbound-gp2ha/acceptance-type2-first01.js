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
      "valueString": "2"
    },
    {
      "name": "acceptanceDate",
      "valueString": "1992-01-14"
    },
    {
      "name": "freeText",
      "valueString": "BABY AT THE REYNOLDS-THORPE CENTRE"
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
            "family": "KENNEDY",
            "given": [
              "SARAH",
              "ANGELA"
            ],
            "prefix": [
              "MISS"
            ]
          }
        ],
        "gender": "female",
        "birthDate": "1991-12-09",
        "address": [
          {
            "line": [
              "??",
              "26 FARMSIDE CLOSE",
              "ST PAULS CRAY",
              "ORPINGTON",
              "KENT"
            ],
            "postalCode": "BR6  7ET",
            "use": "home"
          }
        ],
        "extension": [
          {
            "url": "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-ResidentialInstituteCode",
            "valueString": "RT"
          },
          {
            "url": "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-DrugsDispensedMarker",
            "valueBoolean": true
          },
          {
            "url": "http://hl7.org/fhir/StructureDefinition/patient-birthPlace",
            "valueString": "GLASGOW"
          }
        ],
        "resourceType": "Patient"
      }
    }
  ],
  "resourceType": "Parameters"
}