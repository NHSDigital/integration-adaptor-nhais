package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.*;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.translator.FhirToEdifactManager;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirToEdifactService {

    private final OutboundStateRepository outboundStateRepository;
    private final SequenceService sequenceService;
    private final TimestampService timestampService;
    private final FhirParser fhirParser;
    private final FhirToEdifactManager fhirToEdifactManager;

    public TranslatedInterchange convertToEdifact(Parameters parameters, ReferenceTransactionType.TransactionType transactionType) throws FhirValidationException, EdifactValidationException {
        EdifactToFhirService.TranslationItems translationItems = new EdifactToFhirService.TranslationItems();
        translationItems.parameters = parameters;
        translationItems.patient = fhirParser.getPatientFromParams(parameters);
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

    private void setOperationId(EdifactToFhirService.TranslationItems translationItems) {
        translationItems.operationId = OperationId.buildOperationId(translationItems.recipient, translationItems.transactionNumber);
    }

    private void extractDetailsFromPatient(EdifactToFhirService.TranslationItems translationItems) throws FhirValidationException {
        // set sender and recipient
        translationItems.sender = getSenderTradingPartnerCode(translationItems.patient);
        translationItems.recipient = getRecipientTradingPartnerCode(translationItems.patient);
    }

    private String getSenderTradingPartnerCode(Patient patient) throws FhirValidationException {
        // TODO: should be read from "gpTradingPartnerCode" parameter
        String path = "patient.generalPractitioner";
        exceptionIfMissingOrEmpty(path, patient.getGeneralPractitioner());
        Reference gpReference = patient.getGeneralPractitioner().get(0);
        return getOrganizationIdentifier(path, gpReference);
    }

    private String getHaCipher(Patient patient) throws FhirValidationException {
        String path = "patient.managingOrganization";
        exceptionIfMissingOrEmpty(path, patient.getManagingOrganization());
        Reference haReference = patient.getManagingOrganization();
        return getOrganizationIdentifier(path, haReference);
    }

    private String getRecipientTradingPartnerCode(Patient patient) throws FhirValidationException {
        String haCipher = getHaCipher(patient);
        // TODO: append "1" if three characters and "01" if two characters. throw FhirValidationException otherwise
        return haCipher;
    }

    private String getOrganizationIdentifier(String path, Reference reference) throws FhirValidationException {
        exceptionIfMissingOrEmpty(path, reference);
        path += ".identifier";
        exceptionIfMissingOrEmpty(path, reference.getIdentifier());
        Identifier gpId = reference.getIdentifier();
        exceptionIfMissingOrEmpty(path, gpId);
        path += ".value";
        exceptionIfMissingOrEmpty(path, gpId.getValue());
        return gpId.getValue();
    }

    private void exceptionIfMissingOrEmpty(String path, Object value) throws FhirValidationException {
        if(value == null) {
            throw new FhirValidationException("Missing element at " + path);
        }
        if(value instanceof List) {
            List list = (List) value;
            if(list.isEmpty()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        } else if(value instanceof String) {
            String str = (String) value;
            if(str.isBlank()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        }
    }

    private <T> T castOrError(String path, Class<T> type, Object value) throws FhirValidationException {
        if(!type.isAssignableFrom(value.getClass())) {
            throw new FhirValidationException("Expected " + type.getSimpleName() + " at " + path + " but found " + value.getClass().getSimpleName());
        }
        return type.cast(value);
    }

    private void createSegments(EdifactToFhirService.TranslationItems translationItems) throws FhirValidationException {
        translationItems.segments = new ArrayList<>();
        translationItems.segments.add(new InterchangeHeader(translationItems.sender, translationItems.recipient, translationItems.translationTimestamp));
        translationItems.segments.add(new MessageHeader());
        List<Segment> messageSegments = fhirToEdifactManager.createMessageSegments(translationItems.parameters, translationItems.transactionType);
        translationItems.segments.addAll(messageSegments);
        // numberOfSegments must include the header and trailer thus numberOfSegments = size() + 2
        translationItems.segments.add(new MessageTrailer(messageSegments.size() + 2));
        // outbound interchanges always contain a single message thus numberOfMessages = 1
        translationItems.segments.add(new InterchangeTrailer(1));
    }

    private void prevalidateSegments(EdifactToFhirService.TranslationItems translationItems) throws EdifactValidationException {
        for(Segment segment : translationItems.segments) {
            segment.preValidate();
        }
    }

    private void generateSequenceNumbers(EdifactToFhirService.TranslationItems translationItems) {
        translationItems.sendInterchangeSequence =
                sequenceService.generateInterchangeId(translationItems.sender, translationItems.recipient);
        translationItems.sendMessageSequence =
                sequenceService.generateMessageId(translationItems.sender, translationItems.recipient);
        translationItems.transactionNumber =
                sequenceService.generateTransactionId();
    }

    private void generateTimestamp(EdifactToFhirService.TranslationItems translationItems) {
        translationItems.translationTimestamp = timestampService.getCurrentTimestamp();
    }

    private void recordOutboundState(EdifactToFhirService.TranslationItems translationItems) {
        var outboundState = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setRecipient(translationItems.recipient)
            .setSender(translationItems.sender)

            .setSendInterchangeSequence(translationItems.sendInterchangeSequence)
            .setSendMessageSequence(translationItems.sendMessageSequence)
            .setTransactionId(translationItems.transactionNumber)

            .setTransactionType(translationItems.transactionType.getAbbreviation())
            .setTransactionTimestamp(translationItems.translationTimestamp)
            .setOperationId(translationItems.operationId);
        outboundStateRepository.save(outboundState);
    }

    private void addSequenceNumbersToSegments(EdifactToFhirService.TranslationItems translationItems) {
        for(Segment segment : translationItems.segments) {
            if(segment instanceof InterchangeHeader) {
                InterchangeHeader interchangeHeader = (InterchangeHeader) segment;
                interchangeHeader.setSequenceNumber(translationItems.sendInterchangeSequence);
            } else if(segment instanceof InterchangeTrailer) {
                InterchangeTrailer interchangeTrailer = (InterchangeTrailer) segment;
                interchangeTrailer.setSequenceNumber(translationItems.sendInterchangeSequence);
            } else if(segment instanceof MessageHeader) {
                MessageHeader messageHeader = (MessageHeader) segment;
                messageHeader.setSequenceNumber(translationItems.sendMessageSequence);
            } else if(segment instanceof MessageTrailer) {
                MessageTrailer messageTrailer = (MessageTrailer) segment;
                messageTrailer.setSequenceNumber(translationItems.sendMessageSequence);
            } else if(segment instanceof ReferenceTransactionNumber) {
                ReferenceTransactionNumber referenceTransactionNumber = (ReferenceTransactionNumber) segment;
                referenceTransactionNumber.setTransactionNumber(translationItems.transactionNumber);
            } else if(segment instanceof DateTimePeriod) {
                DateTimePeriod dateTimePeriod = (DateTimePeriod) segment;
                dateTimePeriod.setTimestamp(translationItems.translationTimestamp);
            }
        }
    }

    private TranslatedInterchange translateInterchange(EdifactToFhirService.TranslationItems translationItems) throws EdifactValidationException {
        List<String> segmentStrings = new ArrayList<>(translationItems.segments.size());
        for(Segment segment : translationItems.segments) {
            segmentStrings.add(segment.toEdifact());
        }
        String edifact = String.join("\n", segmentStrings);
        TranslatedInterchange interchange = new TranslatedInterchange();
        interchange.setEdifact(edifact);
        interchange.setInterchangeType(TranslatedInterchange.InterchangeType.REGISTRATION);
        interchange.setOperationId(translationItems.operationId);
        return interchange;
    }

}
