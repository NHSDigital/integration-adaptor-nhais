package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.*;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InboundOperationIdServiceTest {

    private static final Long TN = 15783L;
    private static final String SENDER = "SEN1";
    private static final String RECIPIENT = "REC1";
    private static final String SENDER_OID = OperationId.buildOperationId(SENDER, TN);
    private static final String RECIPIENT_OID = OperationId.buildOperationId(RECIPIENT, TN);

    private final InboundOperationIdService operationIdService = new InboundOperationIdService();

    @Mock
    private Transaction transaction;

    @Mock
    private Message message;

    @Mock
    private Interchange interchange;

    @Mock
    private InterchangeHeader interchangeHeader;

    @Mock
    private ReferenceTransactionNumber referenceTransactionNumber;

    @Mock
    private ReferenceTransactionType referenceTransactionType;

    @BeforeEach
    private void beforeEach() {
        when(transaction.getMessage()).thenReturn(message);
        when(transaction.getReferenceTransactionNumber()).thenReturn(referenceTransactionNumber);
        when(message.getInterchange()).thenReturn(interchange);
        when(message.getReferenceTransactionType()).thenReturn(referenceTransactionType);
        when(interchange.getInterchangeHeader()).thenReturn(interchangeHeader);
        when(referenceTransactionNumber.getTransactionNumber()).thenReturn(TN);
    }

    @ParameterizedTest
    @EnumSource(value = ReferenceTransactionType.Inbound.class, names = {"APPROVAL", "REJECTION"})
    public void When_ApprovalOrRejectionTransaction_Then_OperationIdUsesRecipient(ReferenceTransactionType.Inbound transactionType) {
        when(referenceTransactionType.getTransactionType()).thenReturn(transactionType);
        when(interchangeHeader.getRecipient()).thenReturn(RECIPIENT);
        assertThat(RECIPIENT_OID).isEqualTo(operationIdService.createOperationIdForTransaction(transaction));
    }

    @ParameterizedTest
    @EnumSource(value = ReferenceTransactionType.Inbound.class, names = {"APPROVAL", "REJECTION"}, mode = EnumSource.Mode.EXCLUDE)
    public void When_AllOtherTransactions_Then_OperationIdUsesSender(ReferenceTransactionType.Inbound transactionType) {
        when(referenceTransactionType.getTransactionType()).thenReturn(transactionType);
        when(interchangeHeader.getSender()).thenReturn(SENDER);
        assertThat(SENDER_OID).isEqualTo(operationIdService.createOperationIdForTransaction(transaction));
    }

}
