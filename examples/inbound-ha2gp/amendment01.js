var headers = {
    'TransactionType': 'amendment'
}

var body = {
  "id": "9999999999",
  "gpCode": "4826940,281",
  "gpCipher": "TES5",
  "haCipher": "XX11",
  "healthcarePartyCode": "XX1",
  "patches": [
    // if EDIFACT contains a ?? placeholder we'll include it in the FHIR message
    { "op": "replace", "path": "/address/0/line/0", "value": "??"},
    { "op": "replace", "path": "/address/0/line/1", "value": "136 HIGH STREET"},
    // even blank values are included in the FHIR message
    { "op": "replace", "path": "/address/0/line/2", "value": ""},
    { "op": "replace", "path": "/address/0/line/3", "value": "BROMLEY"},
    { "op": "replace", "path": "/address/0/line/4", "value": "KENT"},
  ]
}
