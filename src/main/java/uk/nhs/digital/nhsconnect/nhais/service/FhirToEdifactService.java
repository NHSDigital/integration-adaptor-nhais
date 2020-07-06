package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.translator.FhirToEdifactSegmentTranslator;
import uk.nhs.digital.nhsconnect.nhais.utils.MissingOrEmptyElementUtils;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirToEdifactService {

    private final OutboundStateRepository outboundStateRepository;
    private final SequenceService sequenceService;
    private final TimestampService timestampService;
    private final FhirToEdifactSegmentTranslator fhirToEdifactSegmentTranslator;

    public OutboundMeshMessage convertToEdifact(Parameters parameters, ReferenceTransactionType.Outbound transactionType) throws FhirValidationException, EdifactValidationException {
        TranslationItems translationItems = new TranslationItems();
        translationItems.parameters = parameters;
        translationItems.patient = new ParametersExtension(parameters).extractPatient();
        translationItems.transactionType = transactionType;
        extractDetailsFromPatient(translationItems);
        generateTimestamp(translationItems);
        createSegments(translationItems);
        prevalidateSegments(translationItems);
        generateSequenceNumbers(translationItems);
        setOperationId(translationItems);
        recordOutboundState(translationItems);
        addSequenceNumbersToSegments(translationItems);
        return translateInterchange(translationItems);
    }

    private void setOperationId(TranslationItems translationItems) {
        translationItems.operationId = OperationId.buildOperationId(translationItems.sender, translationItems.transactionNumber);
    }

    private void extractDetailsFromPatient(TranslationItems translationItems) throws FhirValidationException {
        // set sender and recipient
        translationItems.sender = getSenderTradingPartnerCode(translationItems.parameters);
        translationItems.recipient = getRecipientTradingPartnerCode(translationItems.patient);
    }

    private String getSenderTradingPartnerCode(Parameters parameters) throws FhirValidationException {
        return ParametersExtension.extractValue(parameters, ParameterNames.GP_TRADING_PARTNER_CODE);
    }

    private String getHaCipher(Patient patient) throws FhirValidationException {
        String path = "patient.managingOrganization";
        MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, patient.getManagingOrganization());
        Reference haReference = patient.getManagingOrganization();
        return getOrganizationIdentifier(path, haReference);
    }

    private String getRecipientTradingPartnerCode(Patient patient) throws FhirValidationException {
        String haCipher = getHaCipher(patient);
        if (haCipher.length() == 2) {
            return haCipher + "01";
        } else {
            return haCipher + "1";
        }
    }

    private String getOrganizationIdentifier(String path, Reference reference) throws FhirValidationException {
        MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, reference);
        path += ".identifier";
        MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, reference.getIdentifier());
        Identifier gpId = reference.getIdentifier();
        MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, gpId);
        path += ".value";
        MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, gpId.getValue());
        return gpId.getValue();
    }

    private <T> T castOrError(String path, Class<T> type, Object value) throws FhirValidationException {
        if (!type.isAssignableFrom(value.getClass())) {
            throw new FhirValidationException("Expected " + type.getSimpleName() + " at " + path + " but found " + value.getClass().getSimpleName());
        }
        return type.cast(value);
    }

    private void createSegments(TranslationItems translationItems) throws FhirValidationException {
        translationItems.segments = new ArrayList<>();
        translationItems.segments.add(new InterchangeHeader(translationItems.sender, translationItems.recipient, translationItems.translationTimestamp));
        translationItems.segments.add(new MessageHeader());
        List<Segment> messageSegments = fhirToEdifactSegmentTranslator.createMessageSegments(translationItems.parameters, translationItems.transactionType);
        translationItems.segments.addAll(messageSegments);
        // numberOfSegments must include the header and trailer thus numberOfSegments = size() + 2
        translationItems.segments.add(new MessageTrailer(messageSegments.size() + 2));
        // outbound interchanges always contain a single message thus numberOfMessages = 1
        translationItems.segments.add(new InterchangeTrailer(1));
    }

    private void prevalidateSegments(TranslationItems translationItems) throws EdifactValidationException {
        for (Segment segment : translationItems.segments) {
            segment.preValidate();
        }
    }

    private void generateSequenceNumbers(TranslationItems translationItems) {
        translationItems.sendInterchangeSequence =
            sequenceService.generateInterchangeId(translationItems.sender, translationItems.recipient);
        translationItems.sendMessageSequence =
            sequenceService.generateMessageId(translationItems.sender, translationItems.recipient);
        translationItems.transactionNumber =
            sequenceService.generateTransactionId(translationItems.sender);
    }

    private void generateTimestamp(TranslationItems translationItems) {
        translationItems.translationTimestamp = timestampService.getCurrentTimestamp();
    }

    private void recordOutboundState(TranslationItems translationItems) {
        var outboundState = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setRecipient(translationItems.recipient)
            .setSender(translationItems.sender)

            .setInterchangeSequence(translationItems.sendInterchangeSequence)
            .setMessageSequence(translationItems.sendMessageSequence)
            .setTransactionId(translationItems.transactionNumber)

            .setTransactionType(translationItems.transactionType)
            .setTransactionTimestamp(translationItems.translationTimestamp)
            .setOperationId(translationItems.operationId);
        outboundStateRepository.save(outboundState);
    }

    private void addSequenceNumbersToSegments(TranslationItems translationItems) {
        for (Segment segment : translationItems.segments) {
            if (segment instanceof InterchangeHeader) {
                InterchangeHeader interchangeHeader = (InterchangeHeader) segment;
                interchangeHeader.setSequenceNumber(translationItems.sendInterchangeSequence);
            } else if (segment instanceof InterchangeTrailer) {
                InterchangeTrailer interchangeTrailer = (InterchangeTrailer) segment;
                interchangeTrailer.setSequenceNumber(translationItems.sendInterchangeSequence);
            } else if (segment instanceof MessageHeader) {
                MessageHeader messageHeader = (MessageHeader) segment;
                messageHeader.setSequenceNumber(translationItems.sendMessageSequence);
            } else if (segment instanceof MessageTrailer) {
                MessageTrailer messageTrailer = (MessageTrailer) segment;
                messageTrailer.setSequenceNumber(translationItems.sendMessageSequence);
            } else if (segment instanceof ReferenceTransactionNumber) {
                ReferenceTransactionNumber referenceTransactionNumber = (ReferenceTransactionNumber) segment;
                referenceTransactionNumber.setTransactionNumber(translationItems.transactionNumber);
            } else if (segment instanceof DateTimePeriod) {
                DateTimePeriod dateTimePeriod = (DateTimePeriod) segment;
                if (dateTimePeriod.getTypeAndFormat().equals(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP)) {
                    dateTimePeriod.setTimestamp(translationItems.translationTimestamp);
                }
            }
        }
    }

    private OutboundMeshMessage translateInterchange(TranslationItems translationItems) throws EdifactValidationException {
        List<String> segmentStrings = new ArrayList<>(translationItems.segments.size());
        for (Segment segment : translationItems.segments) {
            segmentStrings.add(segment.toEdifact());
        }
        String edifact = String.join("\n", segmentStrings);
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setContent(edifact);
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setOperationId(translationItems.operationId);
        return meshMessage;
    }

    private static class TranslationItems {
        private Patient patient;
        private Parameters parameters;
        private ReferenceTransactionType.Outbound transactionType;
        private List<Segment> segments = new ArrayList<>();
        private String sender;
        private String recipient;
        private String operationId;
        private Long sendMessageSequence;
        private Long sendInterchangeSequence;
        private Long transactionNumber;
        private Instant translationTimestamp;
    }
}
