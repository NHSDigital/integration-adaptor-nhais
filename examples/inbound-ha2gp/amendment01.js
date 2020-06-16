var headers = {
    'TransactionType': 'amendment'
}

var body = {
  "nhsNumber": "AVERT1/1",
  "gpCode": "4826940,281",
  "gpTradingPartnerCode": "TES5",
  "healthcarePartyCode": "XX1",
  "patches": [
    { "op": "replace", "path": "/name/0/family", "value": "MORRIS" },
    { "op": "replace", "path": "/name/1/family", "value": "ANDREWS" },
    // if EDIFACT contains a ?? placeholder we'll include it in the FHIR message
    { "op": "replace", "path": "/address/0/line/0", "value": "??"},
    { "op": "replace", "path": "/address/0/line/1", "value": "136 HIGH STREET"},
    // even blank values are included in the FHIR message
    { "op": "replace", "path": "/address/0/line/2", "value": ""},
    { "op": "replace", "path": "/address/0/line/3", "value": "BROMLEY"},
    { "op": "replace", "path": "/address/0/line/4", "value": "KENT"},
    { "op": "replace", "path": "/extension/0", "value": {
      "url": "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-DrugsDispensedMarker",
      "valueBoolean": true},
    },
  ]
}
