package uk.nhs.digital.nhsconnect.nhais.outbound;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.time.Instant;
import java.util.List;

public interface CommonTranslationItems {

    List<Segment> getSegments();

    CommonTranslationItems setSegments(List<Segment> segments);

    String getSender();

    CommonTranslationItems setSender(String sender);

    String getRecipient();

    CommonTranslationItems setRecipient(String recipient);

    String getOperationId();

    CommonTranslationItems setOperationId(String operationId);

    Long getSendMessageSequence();

    CommonTranslationItems setSendMessageSequence(Long sendMessageSequence);

    Long getSendInterchangeSequence();

    CommonTranslationItems setSendInterchangeSequence(Long sendInterchangeSequence);

    ReferenceTransactionType.Outbound getTransactionType();

    CommonTranslationItems setTransactionType(ReferenceTransactionType.Outbound transactionType);

    Long getTransactionNumber();

    CommonTranslationItems setTransactionNumber(Long transactionNumber);

    Instant getTranslationTimestamp();

    CommonTranslationItems setTranslationTimestamp(Instant translationTimestamp);

    String getHaCypher();

    CommonTranslationItems setHaCypher(String haCypher);
}
