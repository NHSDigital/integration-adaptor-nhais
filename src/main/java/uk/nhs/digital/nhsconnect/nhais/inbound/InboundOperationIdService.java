package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.springframework.stereotype.Service;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.util.List;

@Service
public class InboundOperationIdService {

    private static final List<ReferenceTransactionType.TransactionType> MAPPED_BY_TRANSACTION_NUMBER =
            List.of(ReferenceTransactionType.Inbound.APPROVAL, ReferenceTransactionType.Inbound.REJECTION);

    public String createOperationIdForTransaction(Transaction transaction) {
        final String tradingPartnerCode;
        final var transactionType = transaction.getMessage().getReferenceTransactionType().getTransactionType();
        final var transactionNumber = transaction.getReferenceTransactionNumber().getTransactionNumber();
        if (MAPPED_BY_TRANSACTION_NUMBER.contains(transactionType)) {
            tradingPartnerCode = transaction.getMessage().getInterchange().getInterchangeHeader().getRecipient();
        } else {
            tradingPartnerCode = transaction.getMessage().getInterchange().getInterchangeHeader().getSender();
        }
        return OperationId.buildOperationId(tradingPartnerCode, transactionNumber);
    }
}
