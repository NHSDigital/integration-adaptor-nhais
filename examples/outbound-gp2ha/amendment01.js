{
  /* The following parameters are required for every Amendment transaction */
  /* TODO: We need to standardise these across all transactions */
  "nhsNumber": "SEQ12",
  "gpCode": "2750922,295",
  "gpTradingPartnerCode": "TES5",
  "healthcarePartyCode": "XX1",
  "freeText": "NOW AT THE ARTHUR ANDREWS CENTRE",
  /* The list of JSONPatches to amend the patient record. Only add, replace, and remove operations are allowed. The add
     and replace operations are treated exactly the same way; that data items will be set to the valid provided
     whether it exists already or not.
     The path must match the examples exactly. Any logically equivalent alternatives are not supported.
     A remove operation must be used to remove data from any patient record that previously had a value. The remove
     operation must only be used on data items for which the GP Links Specification allows a '%' value. */
  "patches": [
    { "op": "replace", "path": "/name/0/prefix/0", "value": "MRS" },
    { "op": "replace", "path": "/name/0/family", "value": "PATTERSON" },
    { "op": "replace", "path": "/name/1/family", "value": "SMYTHE" },
    // If any address line is updated then all five address lines must be provided
    { "op": "replace", "path": "/address/0/line/0", "value": "HOLLY COTTAGE" },
    { "op": "replace", "path": "/address/0/line/1", "value": "12 LONG LANE" },
    { "op": "replace", "path": "/address/0/line/2", "value": "" },
    { "op": "replace", "path": "/address/0/line/3", "value": "BROMLEY" },
    { "op": "replace", "path": "/address/0/line/4", "value": "KENT" },
    { "op": "replace", "path": "/address/0/postalCode", "value": "BR5  4ER" },
    /* For extensions the index in the path is not significant. The adaptor will not care if it is /extension/0 or
       /extension/1.  Only the 'url' property is used to resolve the data item. The entire extension (both url and
       value properties) must be included with an add or replace operation. For a remove operation only the url is
       required */
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
