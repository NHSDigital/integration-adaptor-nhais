package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.EdifactToFhirService;
import uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.EdifactToPatchService;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InboundEdifactTransactionHandlerTest {

    @Mock
    private EdifactToFhirService edifactToFhirService;
    @Mock
    private EdifactToPatchService edifactToPatchService;

    @Mock
    private Transaction transaction;
    @Mock
    private Message message;

    @InjectMocks
    private InboundEdifactTransactionHandler inboundEdifactTransactionHandler;

    @BeforeEach
    void setUp() {
        when(transaction.getMessage()).thenReturn(message);
    }

    @Test
    void whenTranslatingAmendment_expectPatchTranslationServiceIsUsed() {
        when(message.getReferenceTransactionType())
            .thenReturn(new ReferenceTransactionType(ReferenceTransactionType.Inbound.AMENDMENT));
        var amendmentBody = new AmendmentBody();
        when(edifactToPatchService.convertToPatch(transaction)).thenReturn(amendmentBody);

        var dataToSend = inboundEdifactTransactionHandler.translate(transaction);

        assertThat(dataToSend.getContent()).isEqualTo(amendmentBody);

        verify(edifactToPatchService).convertToPatch(transaction);
        verifyNoInteractions(edifactToFhirService);
    }

    @ParameterizedTest
    @EnumSource(names = {"DEDUCTION", "REJECTION", "APPROVAL"})
    void whenTranslatingNonAmendment_expectFhirTranslationServiceIsUsed(ReferenceTransactionType.Inbound transactionType) {
        when(message.getReferenceTransactionType())
            .thenReturn(new ReferenceTransactionType(transactionType));
        var parameters = new Parameters();
        when(edifactToFhirService.convertToFhir(transaction)).thenReturn(parameters);

        var dataToSend = inboundEdifactTransactionHandler.translate(transaction);

        assertThat(dataToSend.getContent()).isEqualTo(parameters);

        verify(edifactToFhirService).convertToFhir(transaction);
        verifyNoInteractions(edifactToPatchService);
    }
}