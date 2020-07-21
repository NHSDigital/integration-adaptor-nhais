package uk.nhs.digital.nhsconnect.nhais.outbound.jsonpatch;

import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.outbound.CommonTranslationItems;

public interface JsonPatchTranslationItems extends CommonTranslationItems {

    AmendmentBody getAmendmentBody();

    JsonPatchTranslationItems setAmendmentBody(AmendmentBody amendmentBody);

}
