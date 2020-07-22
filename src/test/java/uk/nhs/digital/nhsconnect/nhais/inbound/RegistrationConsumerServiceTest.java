package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.queue.InboundGpSystemService;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationConsumerServiceTest {

    public static final long SIS = 3;
    public static final long SMS_1 = 4;
    public static final long SMS_2 = 5;
    public static final long TN_1 = 18L;
    public static final long TN_2 = 19L;
    public static final long TN_3 = 20L;
    public static final long TN_4 = 21L;
    public static final String SENDER = "TES5";
    public static final String RECIPIENT = "XX11";
    public static final ReferenceTransactionType.Inbound MESSAGE_1_TRANSACTION_TYPE = ReferenceTransactionType.Inbound.REJECTION;
    public static final ReferenceTransactionType.Inbound MESSAGE_2_TRANSACTION_TYPE = ReferenceTransactionType.Inbound.APPROVAL;
    public static final long RECEP_INTERCHANGE_SEQUENCE = 100L;
    public static final long RECEP_MESSAGE_SEQUENCE = 200L;
    public static final String RECEP_SENDER = RECIPIENT;
    public static final String RECEP_RECIPIENT = SENDER;
    private static final String MAILBOX_ID = "mailbox";
    private static final String TN_1_OPERATION_ID = "241edf33054d1570ada7fdf1f4cdb3180c0097cf56ab1932ccd111d6cf3f2771";
    private static final String TN_2_OPERATION_ID = "972cca19643ea501d7bd6319c836f7181e1892f01483185bc245284b5a0f7d88";
    private static final String TN_3_OPERATION_ID = "df5c78da3dab361476798142762a6b3da7ee47d6bfb92780883bbb5f5e8a42c9";
    private static final String CONTENT = "some_content";
    private static final Instant MESSAGE_1_TRANSLATION_TIME = ZonedDateTime
        .parse("199201141619", DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(TimestampService.UKZone))
        .toInstant();
    private static final Instant MESSAGE_2_TRANSLATION_TIME = MESSAGE_1_TRANSLATION_TIME.plusSeconds(100);

    private static final Parameters TRANSLATED_TRANSACTION_1 = mock(Parameters.class);
    private static final AmendmentBody TRANSLATED_TRANSACTION_2 = mock(AmendmentBody.class);
    private static final Parameters TRANSLATED_TRANSACTION_3 = mock(Parameters.class);

    private static final String RECEP_AS_EDIFACT = "some_recep_edifact";

    @Mock
    InboundGpSystemService inboundGpSystemService;

    @Mock
    InboundStateRepository inboundStateRepository;

    @Mock
    OutboundStateRepository outboundStateRepository;

    @Mock
    OutboundQueueService outboundQueueService;

    @Mock
    RecepProducerService recepProducerService;

    @Mock
    InboundEdifactTransactionHandler inboundEdifactTransactionHandler;

    @InjectMocks
    RegistrationConsumerService registrationConsumerService;

    @Mock
    Interchange interchange;

    @Mock
    Message message1;

    @Mock
    Message message2;

    @Mock
    Transaction transaction1;

    @Mock
    Transaction transaction2;

    @Mock
    Transaction transaction3;

    @Mock
    Transaction transaction4;

    @Mock
    Interchange recep;

    @Mock
    Message recepMessage;

    @Mock
    EdifactParser edifactParser;

    private void mockInterchangeSegments() {
        when(edifactParser.parse(CONTENT)).thenReturn(interchange);

        when(interchange.getInterchangeHeader()).thenReturn(new InterchangeHeader(SENDER, RECIPIENT, MESSAGE_1_TRANSLATION_TIME).setSequenceNumber(SIS));
        when(interchange.getMessages()).thenReturn(List.of(message1, message2));

        when(message1.getMessageHeader()).thenReturn(new MessageHeader(SMS_1));
        when(message1.getTransactions()).thenReturn(List.of(transaction1, transaction2));
        when(message1.getInterchange()).thenReturn(interchange);
        when(message1.getReferenceTransactionType()).thenReturn(new ReferenceTransactionType(MESSAGE_1_TRANSACTION_TYPE));
        when(message1.getTranslationDateTime()).thenReturn(new DateTimePeriod(MESSAGE_1_TRANSLATION_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));

        when(message2.getMessageHeader()).thenReturn(new MessageHeader(SMS_2));
        when(message2.getTransactions()).thenReturn(List.of(transaction3, transaction4));
        when(message2.getInterchange()).thenReturn(interchange);
        when(message2.getReferenceTransactionType()).thenReturn(new ReferenceTransactionType(MESSAGE_2_TRANSACTION_TYPE));
        when(message2.getTranslationDateTime()).thenReturn(new DateTimePeriod(MESSAGE_2_TRANSLATION_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));

        when(transaction1.getReferenceTransactionNumber()).thenReturn(new ReferenceTransactionNumber(TN_1));
        when(transaction1.getMessage()).thenReturn(message1);
        when(transaction2.getReferenceTransactionNumber()).thenReturn(new ReferenceTransactionNumber(TN_2));
        when(transaction2.getMessage()).thenReturn(message1);
        when(transaction3.getReferenceTransactionNumber()).thenReturn(new ReferenceTransactionNumber(TN_3));
        when(transaction3.getMessage()).thenReturn(message2);
        when(transaction4.getReferenceTransactionNumber()).thenReturn(new ReferenceTransactionNumber(TN_4));
        when(transaction4.getMessage()).thenReturn(message2);

        when(inboundStateRepository.findBy(eq(WorkflowId.REGISTRATION), eq(SENDER), eq(RECIPIENT), eq(SIS), any(), any()))
            .thenReturn(Optional.empty());
        when(inboundStateRepository.findBy(WorkflowId.REGISTRATION, SENDER, RECIPIENT, SIS, SMS_2, TN_4))
            .thenReturn(Optional.of(new InboundState()));
    }

    private void mockRecepSegments() {
        when(recep.getMessages()).thenReturn(List.of(recepMessage));
        when(recep.getInterchangeHeader()).thenReturn(
            new InterchangeHeader(RECEP_SENDER, RECEP_RECIPIENT, MESSAGE_1_TRANSLATION_TIME).setSequenceNumber(RECEP_INTERCHANGE_SEQUENCE));
        when(recepMessage.getInterchange()).thenReturn(recep);
        when(recepMessage.getMessageHeader()).thenReturn(
            new MessageHeader().setSequenceNumber(RECEP_MESSAGE_SEQUENCE));
        when(recepMessage.getTranslationDateTime()).thenReturn(
            new DateTimePeriod(MESSAGE_1_TRANSLATION_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));

        when(recepProducerService.produceRecep(interchange)).thenReturn(RECEP_AS_EDIFACT);
        when(edifactParser.parse(RECEP_AS_EDIFACT)).thenReturn(recep);
    }

    @Test
    public void registrationMessage_publishedToSupplierQueue() {
        when(inboundEdifactTransactionHandler.translate(transaction1))
            .thenReturn(new InboundGpSystemService.DataToSend().setContent(TRANSLATED_TRANSACTION_1));
        when(inboundEdifactTransactionHandler.translate(transaction2))
            .thenReturn(new InboundGpSystemService.DataToSend().setContent(TRANSLATED_TRANSACTION_2));
        when(inboundEdifactTransactionHandler.translate(transaction3))
            .thenReturn(new InboundGpSystemService.DataToSend().setContent(TRANSLATED_TRANSACTION_3));

        mockInterchangeSegments();
        mockRecepSegments();

        var meshInterchangeMessage = new MeshMessage();
        meshInterchangeMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshInterchangeMessage.setContent(CONTENT);

        registrationConsumerService.handleRegistration(meshInterchangeMessage);

        assertInboundMessageHandling();
        assertOutboundRecepProducer();
    }

    private void assertInboundMessageHandling() {
        var dataToSendArgumentCaptor = ArgumentCaptor.forClass(InboundGpSystemService.DataToSend.class);
        verify(inboundGpSystemService, times(3)).publishToSupplierQueue(dataToSendArgumentCaptor.capture());

        var dataToSendValues = dataToSendArgumentCaptor.getAllValues();
        assertThat(dataToSendValues).hasSize(3);
        assertPublishToGpQueue(dataToSendValues.get(0), TN_1_OPERATION_ID, MESSAGE_1_TRANSACTION_TYPE, TRANSLATED_TRANSACTION_1);
        assertPublishToGpQueue(dataToSendValues.get(1), TN_2_OPERATION_ID, MESSAGE_1_TRANSACTION_TYPE, TRANSLATED_TRANSACTION_2);
        assertPublishToGpQueue(dataToSendValues.get(2), TN_3_OPERATION_ID, MESSAGE_2_TRANSACTION_TYPE, TRANSLATED_TRANSACTION_3);

        var inboundStateArgumentCaptor = ArgumentCaptor.forClass(InboundState.class);
        verify(inboundStateRepository, times(3)).save(inboundStateArgumentCaptor.capture());

        var inboundStateValues = inboundStateArgumentCaptor.getAllValues();
        assertThat(inboundStateValues).hasSize(3);
        assertInboundState(inboundStateValues.get(0), SMS_1, TN_1, MESSAGE_1_TRANSACTION_TYPE, MESSAGE_1_TRANSLATION_TIME);
        assertInboundState(inboundStateValues.get(1), SMS_1, TN_2, MESSAGE_1_TRANSACTION_TYPE, MESSAGE_1_TRANSLATION_TIME);
        assertInboundState(inboundStateValues.get(2), SMS_2, TN_3, MESSAGE_2_TRANSACTION_TYPE, MESSAGE_2_TRANSLATION_TIME);
    }

    private void assertOutboundRecepProducer() {
        var outboundStateArgumentCaptor = ArgumentCaptor.forClass(OutboundState.class);
        verify(outboundStateRepository).save(outboundStateArgumentCaptor.capture());
        var savedRecepOutboundState = outboundStateArgumentCaptor.getValue();
        var expectedRecepOutboundState = new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender(RECEP_SENDER)
            .setRecipient(RECEP_RECIPIENT)
            .setInterchangeSequence(RECEP_INTERCHANGE_SEQUENCE)
            .setMessageSequence(RECEP_MESSAGE_SEQUENCE)
            .setTranslationTimestamp(MESSAGE_1_TRANSLATION_TIME);

        assertThat(savedRecepOutboundState).isEqualToIgnoringGivenFields(expectedRecepOutboundState, "id");

        var meshRecepMessageArgumentCaptor = ArgumentCaptor.forClass(MeshMessage.class);
        verify(outboundQueueService).publish(meshRecepMessageArgumentCaptor.capture());

        var sentRecep = meshRecepMessageArgumentCaptor.getValue();
        assertThat(sentRecep.getWorkflowId()).isEqualTo(WorkflowId.RECEP);
        assertThat(sentRecep.getContent()).isEqualTo(RECEP_AS_EDIFACT);
        assertThat(sentRecep.getHaTradingPartnerCode()).isEqualTo(RECEP_RECIPIENT);
    }

    private void assertPublishToGpQueue(
        InboundGpSystemService.DataToSend dataToSend,
        String operationId,
        ReferenceTransactionType.TransactionType transactionType,
        Object content) {

        assertThat(dataToSend.getOperationId()).isEqualTo(operationId);
        assertThat(dataToSend.getTransactionType()).isEqualTo(transactionType);
        assertThat(dataToSend.getContent()).isEqualTo(content);
    }

    private void assertInboundState(
        InboundState savedInboundState, long sms, long tn, ReferenceTransactionType.TransactionType transactionType, Instant translationTyime) {

        assertThat(savedInboundState.getWorkflowId()).isEqualTo(WorkflowId.REGISTRATION);
        assertThat(savedInboundState.getSender()).isEqualTo(SENDER);
        assertThat(savedInboundState.getRecipient()).isEqualTo(RECIPIENT);
        assertThat(savedInboundState.getInterchangeSequence()).isEqualTo(SIS);
        assertThat(savedInboundState.getMessageSequence()).isEqualTo(sms);
        assertThat(savedInboundState.getTransactionId()).isEqualTo(tn);
        assertThat(savedInboundState.getTransactionType().getCode()).isEqualTo(transactionType.getCode());
        assertThat(savedInboundState.getTranslationTimestamp()).isEqualTo(translationTyime);
    }

    @Test
    void testErrorsDuringParsingMesh() {
        when(edifactParser.parse(any())).thenThrow(ToEdifactParsingException.class);

        assertThatThrownBy(() -> registrationConsumerService.handleRegistration(new MeshMessage()))
            .isExactlyInstanceOf(ToEdifactParsingException.class);

        verifyNoInteractions(inboundGpSystemService);
        verifyNoInteractions(outboundStateRepository);
        verifyNoInteractions(outboundQueueService);
    }
}
