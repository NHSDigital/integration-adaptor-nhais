package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.springframework.stereotype.Service;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.util.Set;

@Service
public class InboundOperationIdService {

    private static final Set<ReferenceTransactionType.TransactionType> MATCHED_BY_TRANSACTION_NUMBER =
            Set.of(ReferenceTransactionType.Inbound.APPROVAL, ReferenceTransactionType.Inbound.REJECTION);

    public String createOperationIdForTransaction(Transaction transaction) {
        final String tradingPartnerCode;
        final var transactionType = transaction.getMessage().getReferenceTransactionType().getTransactionType();
        final var transactionNumber = transaction.getReferenceTransactionNumber().getTransactionNumber();
        if (MATCHED_BY_TRANSACTION_NUMBER.contains(transactionType)) {
            // inbound transactions matched to the outbound transaction by their transaction number use recipient
            // trading partner code to ensure the same OperationId is generated
            tradingPartnerCode = transaction.getMessage().getInterchange().getInterchangeHeader().getRecipient();
        } else {
            // inbound transactions not matched to an outbound transaction by transaction number use sender
            // trading partner code to ensure the OperationId is unique
            tradingPartnerCode = transaction.getMessage().getInterchange().getInterchangeHeader().getSender();
        }
        return OperationId.buildOperationId(tradingPartnerCode, transactionNumber);
    }
}
