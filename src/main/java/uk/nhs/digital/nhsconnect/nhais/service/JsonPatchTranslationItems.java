package uk.nhs.digital.nhsconnect.nhais.service;

import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;

public interface JsonPatchTranslationItems extends CommonTranslationItems{

    AmendmentBody getAmendmentBody();

    JsonPatchTranslationItems setAmendmentBody(AmendmentBody amendmentBody);

}
