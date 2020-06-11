{
  "id": "SEQ12",
  "gpCode": "2750922,295",
  "gpCipher": "TES5",
  "haCipher": "XX11",
  "gpFreeText": "NOW AT THE ARTHUR ANDREWS CENTRE",
  "patches": [
    // add and replace operations are treated exactly the same
    // The path must match the example exactly. Any logically equivalent alternatives are not supported
    { "op": "replace", "path": "/name/0/prefix/0", "value": "MRS" },
    { "op": "replace", "path": "/name/0/family", "value": "PATTERSON" },
    { "op": "replace", "path": "/name/1/family", "value": "SMYTHE"},
    { "op": "replace", "path": "/address/0/line/0", "value": "HOLLY COTTAGE"},
    { "op": "replace", "path": "/address/0/line/1", "value": "12 LONG LANE"},
    // Note that /address/0/line/2 is omitted - there is no Locality
    { "op": "replace", "path": "/address/0/line/3", "value": "BROMLEY"},
    { "op": "replace", "path": "/address/0/line/4", "value": "KENT"},
    { "op": "replace", "path": "/address/0/postalCode", "value": "BR5  4ER" },
    // for extensions the index is not significant
    // the entire extension must be included with add or replace (both url and value properties)
    // for remove only the url is required
    { "op": "replace", "path": "/extension/0", "value": {
      "url": "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-DrugsDispensedMarker",
      "valueBoolean": true},
    },
    { "op": "replace", "path": "/extension/0", "value": {
      "url": "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-ResidentialInstituteCode",
      "valueString": "AA"}
    }
  ]
}
