package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_message",
        def = "{'receiveInterchangeSequence' : 1, 'receiveMessageSequence': 1, 'sender': 1, 'recipient': 1}",
        unique = true)
})
@Data
@Document
public class InboundState {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private DataType dataType;
    private Long receiveInterchangeSequence;
    private Long receiveMessageSequence;
    private String sender;
    private String recipient;
    private Long transactionNumber;
    private Instant translationTimestamp;
    private ReferenceTransactionType.TransactionType transactionType;

    public static InboundState fromInterchange(Interchange interchange) {
        //TODO initial assumption that interchange can have a single message only

        var interchangeHeader = interchange.getInterchangeHeader();
        var translationDateTime = interchange.getTranslationDateTime();
        var messageHeader = interchange.getMessageHeader();
        var referenceTransactionNumber = interchange.getReferenceTransactionNumber();
        var referenceTransactionType = interchange.getReferenceTransactionType();

        return new InboundState()
            .setDataType(DataType.INTERCHANGE)
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setReceiveInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setReceiveMessageSequence(messageHeader.getSequenceNumber())
            .setTransactionNumber(referenceTransactionNumber.getTransactionNumber())
            .setTransactionType(referenceTransactionType.getTransactionType())
            .setTranslationTimestamp(translationDateTime.getTimestamp());
    }

    public static InboundState fromRecep(Recep recep) {
        var interchangeHeader = recep.getInterchangeHeader();
        var dateTimePeriod = recep.getDateTimePeriod();

        return new InboundState()
            .setDataType(DataType.RECEP)
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setReceiveInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setTranslationTimestamp(dateTimePeriod.getTimestamp());
    }
}
