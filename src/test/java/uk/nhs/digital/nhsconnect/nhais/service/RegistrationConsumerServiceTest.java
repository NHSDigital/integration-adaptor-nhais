package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationConsumerServiceTest {

    public static final long INTERCHANGE_SEQUENCE = 3L;
    public static final long MESSAGE_SEQUENCE = 4L;
    public static final String SENDER = "TES5";
    public static final String RECIPIENT = "XX11";
    public static final long TRANSACTION_NUMBER = 18L;
    public static final ReferenceTransactionType.TransactionType TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.ACCEPTANCE;
    public static final long RECEP_INTERCHANGE_SEQUENCE = 100L;
    public static final long RECEP_MESSAGE_SEQUENCE = 200L;
    public static final String RECEP_SENDER = RECIPIENT;
    public static final String RECEP_RECIPIENT = SENDER;
    private static final String OPERATION_ID = "70086e87f012c1e9776bd59589726d3722420823e2b5ceb2f7c7441c4044ffba";
    private static final String CONTENT = "some_content";
    private static final Instant TRANSLATION_TIME = ZonedDateTime
        .parse("199201141619", DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(TimestampService.UKZone))
        .toInstant();

    private static final String RECEP_AS_EDIFACT = "some_recep_edifact";

    @Mock
    InboundGpSystemService inboundGpSystemService;

    @Mock
    InboundStateRepository inboundStateRepository;

    @Mock
    OutboundStateRepository outboundStateRepository;

    @Mock
    OutboundMeshService outboundMeshService;

    @Mock
    RecepProducerService recepProducerService;

    @InjectMocks
    RegistrationConsumerService registrationConsumerService;

    @Mock
    Recep recep;

    @Mock
    Interchange interchange;

    @Mock
    EdifactParser edifactParser;

    private void mockInterchangeSegments() {
        when(edifactParser.parse(CONTENT)).thenReturn(interchange);

        when(interchange.getInterchangeHeader()).thenReturn(
            new InterchangeHeader(SENDER, RECIPIENT, TRANSLATION_TIME).setSequenceNumber(INTERCHANGE_SEQUENCE));
        when(interchange.getMessageHeader()).thenReturn(
            new MessageHeader(MESSAGE_SEQUENCE));
        when(interchange.getReferenceTransactionNumber()).thenReturn(
            new ReferenceTransactionNumber(TRANSACTION_NUMBER));
        when(interchange.getReferenceTransactionType()).thenReturn(
            new ReferenceTransactionType(TRANSACTION_TYPE));
        when(interchange.getTranslationDateTime()).thenReturn(
            new DateTimePeriod(TRANSLATION_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
    }

    private void mockInterchangeRecepRequiredSegments() {
        when(interchange.getHealthAuthorityNameAndAddress()).thenReturn(
            new HealthAuthorityNameAndAddress("identifier", "code"));
        when(interchange.getGpNameAndAddress()).thenReturn(
            new GpNameAndAddress("identifier", "code"));

        when(recep.getInterchangeHeader()).thenReturn(
            new InterchangeHeader(RECEP_SENDER, RECEP_RECIPIENT, TRANSLATION_TIME).setSequenceNumber(RECEP_INTERCHANGE_SEQUENCE));
        when(recep.getMessageHeader()).thenReturn(
            new MessageHeader().setSequenceNumber(RECEP_MESSAGE_SEQUENCE));
        when(recep.getDateTimePeriod()).thenReturn(
            new DateTimePeriod(TRANSLATION_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));

        when(recep.toEdifact()).thenReturn(RECEP_AS_EDIFACT);
    }

    @Test
    public void registrationMessage_publishedToSupplierQueue() {
        mockInterchangeSegments();
        mockInterchangeRecepRequiredSegments();

        when(recepProducerService.produceRecep(interchange)).thenReturn(recep);

        var meshInterchangeMessage = new MeshMessage();
        meshInterchangeMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshInterchangeMessage.setContent(CONTENT);

        registrationConsumerService.handleRegistration(meshInterchangeMessage);

        var inboundStateArgumentCaptor = ArgumentCaptor.forClass(InboundState.class);
        verify(inboundStateRepository).save(inboundStateArgumentCaptor.capture());
        var savedInboundState = inboundStateArgumentCaptor.getValue();
        assertThat(savedInboundState.getWorkflowId()).isEqualTo(WorkflowId.REGISTRATION);
        assertThat(savedInboundState.getSender()).isEqualTo(SENDER);
        assertThat(savedInboundState.getRecipient()).isEqualTo(RECIPIENT);
        assertThat(savedInboundState.getReceiveInterchangeSequence()).isEqualTo(INTERCHANGE_SEQUENCE);
        assertThat(savedInboundState.getReceiveMessageSequence()).isEqualTo(MESSAGE_SEQUENCE);
        assertThat(savedInboundState.getTransactionNumber()).isEqualTo(TRANSACTION_NUMBER);
        assertThat(savedInboundState.getTransactionType().getCode()).isEqualTo(TRANSACTION_TYPE.getCode());
        assertThat(savedInboundState.getTranslationTimestamp()).isEqualTo(TRANSLATION_TIME);

        var outboundStateArgumentCaptor = ArgumentCaptor.forClass(OutboundState.class);
        verify(outboundStateRepository).save(outboundStateArgumentCaptor.capture());
        var savedRecepOutboundState = outboundStateArgumentCaptor.getValue();
        var expectedRecepOutboundState = new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender(RECEP_SENDER)
            .setRecipient(RECEP_RECIPIENT)
            .setSendInterchangeSequence(RECEP_INTERCHANGE_SEQUENCE)
            .setSendMessageSequence(RECEP_MESSAGE_SEQUENCE)
            .setTransactionTimestamp(TRANSLATION_TIME);

        assertThat(savedRecepOutboundState).isEqualToIgnoringGivenFields(expectedRecepOutboundState, "id");

        verify(inboundGpSystemService).publishToSupplierQueue(any(), eq(OPERATION_ID), eq(TRANSACTION_TYPE));

        var meshRecepMessageArgumentCaptor = ArgumentCaptor.forClass(MeshMessage.class);
        verify(outboundMeshService).publishToOutboundQueue(meshRecepMessageArgumentCaptor.capture());

        var sentRecep = meshRecepMessageArgumentCaptor.getValue();
        assertThat(sentRecep.getWorkflowId()).isEqualTo(WorkflowId.RECEP);
        assertThat(sentRecep.getContent()).isEqualTo(RECEP_AS_EDIFACT);
    }

    @Test
    public void whenDuplicateMessageErrorOnInboundStateSave_thenNotPublishedToSupplierQueue() {
        mockInterchangeSegments();

        when(interchange.getInterchangeHeader()).thenReturn(
            new InterchangeHeader(SENDER, RECIPIENT, TRANSLATION_TIME).setSequenceNumber(INTERCHANGE_SEQUENCE));
        when(interchange.getMessageHeader()).thenReturn(
            new MessageHeader(MESSAGE_SEQUENCE));
        when(interchange.getReferenceTransactionNumber()).thenReturn(
            new ReferenceTransactionNumber(TRANSACTION_NUMBER));
        when(interchange.getReferenceTransactionType()).thenReturn(
            new ReferenceTransactionType(TRANSACTION_TYPE));
        when(interchange.getTranslationDateTime()).thenReturn(
            new DateTimePeriod(TRANSLATION_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));


        var meshInterchangeMessage = new MeshMessage();
        meshInterchangeMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshInterchangeMessage.setContent(CONTENT);

        when(inboundStateRepository.save(any())).thenThrow(DuplicateKeyException.class);

        registrationConsumerService.handleRegistration(meshInterchangeMessage);

        verifyNoInteractions(inboundGpSystemService);
        verifyNoInteractions(outboundStateRepository);
        verifyNoInteractions(outboundMeshService);
    }

    @Test
    void testErrorsDuringParsingMesh() {
        when(edifactParser.parse(any())).thenThrow(ToEdifactParsingException.class);

        assertThatThrownBy(() -> registrationConsumerService.handleRegistration(new MeshMessage()))
            .isExactlyInstanceOf(ToEdifactParsingException.class);

        verifyNoInteractions(inboundGpSystemService);
        verifyNoInteractions(outboundStateRepository);
        verifyNoInteractions(outboundMeshService);
    }
}
