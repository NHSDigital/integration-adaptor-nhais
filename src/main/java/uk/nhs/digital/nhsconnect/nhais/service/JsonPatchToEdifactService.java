package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.AmendmentToEdifactTranslator;

import java.util.List;

@Component
public class JsonPatchToEdifactService extends AbstractToEdifactService<JsonPatchTranslationItems> {

    private final AmendmentToEdifactTranslator amendmentTranslator;

    @Autowired
    public JsonPatchToEdifactService(
        SequenceService sequenceService,
        TimestampService timestampService,
        OutboundStateRepository outboundStateRepository,
        AmendmentToEdifactTranslator amendmentTranslator) {

        super(sequenceService, timestampService, outboundStateRepository);
        this.amendmentTranslator = amendmentTranslator;
    }

    public OutboundMeshMessage convertToEdifact(AmendmentBody amendmentBody) throws FhirValidationException, EdifactValidationException {
        JsonPatchTranslationItems translationItems = new TranslationItems();
        translationItems.setAmendmentBody(amendmentBody);
        translationItems.setSender(amendmentBody.getGpTradingPartnerCode());
        translationItems.setRecipient(getRecipientTradingPartnerCode(amendmentBody.getHealthcarePartyCode()));
        translationItems.setTransactionType(ReferenceTransactionType.Outbound.AMENDMENT);

        return convert(translationItems);
    }

    @Override
    protected List<Segment> createMessageSegments(JsonPatchTranslationItems translationItems) {
        return amendmentTranslator.translate(translationItems.getAmendmentBody());
    }
}
