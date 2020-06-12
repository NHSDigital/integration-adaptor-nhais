{
  "id": "9999999999",
  "gpCode": "8880232,G82697",
  "gpCipher": "CHC5",
  "haCipher": "KC01",
  "healthcarePartyCode": "KC",
  "patches": [
    // GP Links requires a ?? placeholder if there is no house name
    { "op": "replace", "path": "/address/0/line/0", "value": "??"},
    { "op": "replace", "path": "/address/0/line/1", "value": "19 ZZZZZZZZ HOUSE"},
    { "op": "replace", "path": "/address/0/line/2", "value": "ZZZZZ DRIVE"},
    { "op": "replace", "path": "/address/0/line/3", "value": "ZZZZZZZZZZ"},
    { "op": "replace", "path": "/address/0/line/4", "value": "KENT"},
  ]
}
