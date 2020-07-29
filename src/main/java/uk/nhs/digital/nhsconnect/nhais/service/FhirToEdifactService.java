package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshCypherDecoder;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.translator.FhirToEdifactSegmentTranslator;
import uk.nhs.digital.nhsconnect.nhais.utils.FhirElementsUtils;

import java.util.List;

@Component
public class FhirToEdifactService extends AbstractToEdifactService<FhirTranslationItems>{

    private final FhirToEdifactSegmentTranslator fhirToEdifactSegmentTranslator;

    @Autowired
    public FhirToEdifactService(OutboundStateRepository outboundStateRepository,
                                SequenceService sequenceService,
                                TimestampService timestampService,
                                FhirToEdifactSegmentTranslator fhirToEdifactSegmentTranslator,
                                MeshCypherDecoder meshCypherDecoder) {
        super(sequenceService, timestampService, outboundStateRepository, meshCypherDecoder);
        this.fhirToEdifactSegmentTranslator = fhirToEdifactSegmentTranslator;
    }

    public OutboundMeshMessage convertToEdifact(Parameters parameters, ReferenceTransactionType.Outbound transactionType) throws FhirValidationException, EdifactValidationException {
        FhirTranslationItems translationItems = new TranslationItems();
        translationItems.setParameters(parameters);
        translationItems.setSender(getSenderTradingPartnerCode(translationItems.getParameters()));
        String haCipher = getHaCipher(new ParametersExtension(parameters).extractPatient());
        translationItems.setRecipient(getRecipientTradingPartnerCode(haCipher));
        translationItems.setTransactionType(transactionType);

        return convert(translationItems);
    }

    private String getSenderTradingPartnerCode(Parameters parameters) throws FhirValidationException {
        return ParametersExtension.notBlankValue(parameters, ParameterNames.GP_TRADING_PARTNER_CODE);
    }

    private String getHaCipher(Patient patient) throws FhirValidationException {
        FhirElementsUtils.checkHaCipherPresence(patient);
        Reference haReference = patient.getManagingOrganization();
        return getOrganizationIdentifier(haReference);
    }

    private String getOrganizationIdentifier(Reference reference) throws FhirValidationException {
        Identifier gpId = reference.getIdentifier();
        return gpId.getValue();
    }

    @Override
    protected List<Segment> createMessageSegments(FhirTranslationItems translationItems) throws FhirValidationException {
        return fhirToEdifactSegmentTranslator.createMessageSegments(translationItems.getParameters(), translationItems.getTransactionType());
    }

}
