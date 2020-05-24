package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateDAO;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FhirToEdifactService {

    // TODO: remove and replace with Segment abstract class when merged
    public interface Segment {
        String toEdifact();
        void prevalidate();
    }

    public enum TransactionType {} // TODO: remove and replace with edifact model when implemented

    @Autowired
    private OutboundStateRepository outboundStateRepository;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private TimestampService timestampService;

    public TranslatedInterchange convertToEdifact(Patient patient, String operationId, TransactionType transactionType) throws FhirValidationException {
        TranslationItems translationItems = new TranslationItems();
        translationItems.patient = patient;
        translationItems.operationId = operationId;
        translationItems.transactionType = transactionType;
        extractDetailsFromPatient(translationItems);
        createSegments(translationItems);
        prevalidateSegments(translationItems);
        generateSequenceNumbers(translationItems);
        generateTimestamp(translationItems);
        recordOutboundState(translationItems);
        addSequenceNumbersToSegments(translationItems);
        return translateInterchange(translationItems);
    }

    private void extractDetailsFromPatient(TranslationItems translationItems) throws FhirValidationException {
        // set sender and recipient
        translationItems.sender = getSender(translationItems.patient);
        translationItems.recipient = getRecipient(translationItems.patient);
    }

    private String getSender(Patient patient) throws FhirValidationException {
        String path = "patient.generalPractitioner";
        exceptionIfMissingOrEmpty(path, patient.getGeneralPractitioner());
        Reference gpReference = patient.getGeneralPractitioner().get(0);
        return getOrganizationIdentifier(path, gpReference);
    }

    private String getRecipient(Patient patient) throws FhirValidationException {
        String path = "patient.managingOrganization";
        exceptionIfMissingOrEmpty(path, patient.getManagingOrganization());
        Reference haReference = patient.getManagingOrganization();
        return getOrganizationIdentifier(path, haReference);
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

    private void createSegments(TranslationItems translationItems) {
        // TODO: create each segment needed for EDIFACT state MVC (those created by Python app)
        translationItems.segments = Collections.emptyList();
    }

    private void prevalidateSegments(TranslationItems translationItems) {
        // TODO: add correct throws declaration for actual prevalidate implementation
        for(Segment segment : translationItems.segments) {
            segment.prevalidate();
        }
    }

    private void generateSequenceNumbers(TranslationItems translationItems) {
        translationItems.sendInterchangeSequence =
                sequenceService.generateInterchangeId(translationItems.sender, translationItems.recipient);
        translationItems.sendMessageSequence =
                sequenceService.generateMessageId(translationItems.sender, translationItems.recipient);
        translationItems.transactionNumber =
                sequenceService.generateTransactionId();
    }

    private void generateTimestamp(TranslationItems translationItems) {
        translationItems.translationTimestamp = timestampService.getCurrentTimestamp();
    }

    private void recordOutboundState(TranslationItems translationItems) {
        OutboundStateDAO outboundStateDAO = new OutboundStateDAO();
        outboundStateDAO.setRecipient(translationItems.recipient);
        outboundStateDAO.setSender(translationItems.sender);

        outboundStateDAO.setSendInterchangeSequence(translationItems.sendInterchangeSequence);
        outboundStateDAO.setSendMessageSequence(translationItems.sendMessageSequence);
        outboundStateDAO.setTransactionId(translationItems.transactionNumber);

//      TODO: Record three letter transaction type e.g. ACG
//        outboundStateDAO.setTransactionType();
        outboundStateDAO.setTransactionTimestamp(Date.from(translationItems.translationTimestamp.toInstant()));
        outboundStateDAO.setOperationId(translationItems.operationId);
        outboundStateRepository.save(outboundStateDAO);
    }

    private void addSequenceNumbersToSegments(TranslationItems translationItems) {
        for(Segment segment : translationItems.segments) {
            // if instanceof SomethingWithState
            // set stateful properties
            // else if isntanceof SomethingElseWithState
            // ...
        }
    }

    private TranslatedInterchange translateInterchange(TranslationItems translationItems) {
        // toEdifact() also calls validate()
        String edifact = translationItems.segments.stream().map(Segment::toEdifact).collect(Collectors.joining("\n"));
        TranslatedInterchange interchange = new TranslatedInterchange();
        interchange.setEdifact(edifact);
        return interchange;
    }

    private static class TranslationItems {
        private Patient patient;
        private TransactionType transactionType;
        private List<Segment> segments = new ArrayList<>();
        private String sender;
        private String recipient;
        private String operationId;
        private Long sendMessageSequence;
        private Long sendInterchangeSequence;
        private Long transactionNumber;
        private ZonedDateTime translationTimestamp;
    }

}
