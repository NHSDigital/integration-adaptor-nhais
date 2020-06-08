package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifact;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.*;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirToEdifactService {

    private final FhirParser fhirParser;
    private final OutboundStateRepository outboundStateRepository;
    private final SequenceService sequenceService;
    private final TimestampService timestampService;

    public TranslatedInterchange convertToEdifact(Parameters parameters, ReferenceTransactionType.TransactionType transactionType) throws FhirValidationException, EdifactValidationException {
        TranslationItems translationItems = new TranslationItems();
        translationItems.patient = fhirParser.getPatientFromParams(parameters);
        translationItems.transactionType = transactionType;
        extractDetailsFromPatient(translationItems);
        generateTimestamp(translationItems);
        createSegments(parameters, translationItems);
        prevalidateSegments(translationItems);
        generateSequenceNumbers(translationItems);
        setOperationId(translationItems);
        recordOutboundState(translationItems);
        addSequenceNumbersToSegments(translationItems);
        return translateInterchange(translationItems);
    }

    private void setOperationId(TranslationItems translationItems) {
        translationItems.operationId = OperationId.buildOperationId(translationItems.recipient, translationItems.transactionNumber);
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
        if (value == null) {
            throw new FhirValidationException("Missing element at " + path);
        }
        if (value instanceof List) {
            List list = (List) value;
            if (list.isEmpty()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        } else if (value instanceof String) {
            String str = (String) value;
            if (str.isBlank()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        }
    }

    private <T> T castOrError(String path, Class<T> type, Object value) throws FhirValidationException {
        if (!type.isAssignableFrom(value.getClass())) {
            throw new FhirValidationException("Expected " + type.getSimpleName() + " at " + path + " but found " + value.getClass().getSimpleName());
        }
        return type.cast(value);
    }

    private void createSegments(Parameters parameters, TranslationItems translationItems) {
        FromFhirToEdifact fromFhirToEdifact = new FromFhirToEdifact();
        List<Segment> segmentsFromFhir = fromFhirToEdifact.map(parameters);

        translationItems.segments = Arrays.asList(
                new InterchangeHeader(translationItems.sender, translationItems.recipient, translationItems.translationTimestamp),
                new MessageHeader(),
                new BeginningOfMessage());
        translationItems.segments.addAll(segmentsFromFhir);
        translationItems.segments.addAll(Arrays.asList(
                new SegmentGroup(1),
                new ReferenceTransactionNumber(),
                new MessageTrailer(8),
                new InterchangeTrailer(1)
        ));

//                new NameAndAddress(translationItems.recipient, NameAndAddress.QualifierAndCode.FHS),
//                new DateTimePeriod(translationItems.translationTimestamp, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP),
//                new ReferenceTransactionType(translationItems.transactionType),
//                new SegmentGroup(1),
//                new ReferenceTransactionNumber(),
//                new MessageTrailer(8),
//                new InterchangeTrailer(1)
//        );
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
                sequenceService.generateTransactionId();
    }

    private void generateTimestamp(TranslationItems translationItems) {
        translationItems.translationTimestamp = timestampService.getCurrentTimestamp();
    }

    private void recordOutboundState(TranslationItems translationItems) {
        var outboundState = new OutboundState()
                .setRecipient(translationItems.recipient)
                .setSender(translationItems.sender)

                .setSendInterchangeSequence(translationItems.sendInterchangeSequence)
                .setSendMessageSequence(translationItems.sendMessageSequence)
                .setTransactionId(translationItems.transactionNumber)

                .setTransactionType(translationItems.transactionType.getAbbreviation())
                .setTransactionTimestamp(Date.from(translationItems.translationTimestamp))
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
            }
        }
    }

    private TranslatedInterchange translateInterchange(TranslationItems translationItems) throws EdifactValidationException {
        List<String> segmentStrings = new ArrayList<>(translationItems.segments.size());
        for (Segment segment : translationItems.segments) {
            segmentStrings.add(segment.toEdifact());
        }
        String edifact = String.join("\n", segmentStrings);
        TranslatedInterchange interchange = new TranslatedInterchange();
        interchange.setEdifact(edifact);
        interchange.setInterchangeType(TranslatedInterchange.InterchangeType.REGISTRATION);
        interchange.setOperationId(translationItems.operationId);
        return interchange;
    }

    private static class TranslationItems {
        private Patient patient;
        private ReferenceTransactionType.TransactionType transactionType;
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
