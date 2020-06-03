package uk.nhs.digital.nhsconnect.nhais.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
public class RegistrationConsumerServiceTest {

    @Mock
    InboundGpSystemService inboundGpSystemService;

    @Mock
    InboundStateRepository inboundStateRepository;

    @InjectMocks
    RegistrationConsumerService registrationConsumerService;

    private final String exampleMessage = "UNB+UNOA:2+TES5+XX11+020114:1619+00000003'\n" +
        "UNH+00000004+FHSREG:0:1:FH:FHS001'\n" +
        "BGM+++507'\n" +
        "NAD+FHS+XX1:954'\n" +
        "DTM+137:199201141619:203'\n" +
        "RFF+950:G1'\n" +
        "S01+1'\n" +
        "RFF+TN:18'\n" +
        "NAD+GP+2750922,295:900'\n" +
        "NAD+RIC+RT:956'\n" +
        "QTY+951:6'\n" +
        "QTY+952:3'\n" +
        "HEA+ACD+A:ZZZ'\n" +
        "HEA+ATP+2:ZZZ'\n" +
        "HEA+BM+S:ZZZ'\n" +
        "HEA+DM+Y:ZZZ'\n" +
        "DTM+956:19920114:102'\n" +
        "LOC+950+GLASGOW'\n" +
        "FTX+RGI+++BABY AT THE REYNOLDS-THORPE CENTRE'\n" +
        "S02+2'\n" +
        "PNA+PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'\n" +
        "DTM+329:19911209:102'\n" +
        "PDI+2'\n" +
        "NAD+PAT++??:26 FARMSIDE CLOSE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7ET'\n" +
        "UNT+24+00000004'\n" +
        "UNZ+1+00000003'";

    @Test
    public void registrationMessage_publishedToSupplierQueue() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setContent(exampleMessage);

        registrationConsumerService.handleRegistration(meshMessage);

        ArgumentCaptor<InboundState> inboundStateArgumentCaptor = ArgumentCaptor.forClass(InboundState.class);
        verify(inboundStateRepository).save(inboundStateArgumentCaptor.capture());
        InboundState savedInboundState = inboundStateArgumentCaptor.getValue();
        Instant expectedTime = ZonedDateTime.parse("020114:1619", DateTimeFormatter.ofPattern("yyMMdd:HHmm").withZone(TimestampService.UKZone)).toInstant();
        assertThat(savedInboundState.getSender()).isEqualTo("TES5");
        assertThat(savedInboundState.getRecipient()).isEqualTo("XX11");
        assertThat(savedInboundState.getReceiveInterchangeSequence()).isEqualTo(3L);
        assertThat(savedInboundState.getReceiveMessageSequence()).isEqualTo(4L);
        assertThat(savedInboundState.getTransactionNumber()).isEqualTo(18L);
        assertThat(savedInboundState.getTransactionType().getCode()).isEqualTo("G1");
        assertThat(savedInboundState.getTranslationTimestamp()).isEqualTo(expectedTime);

        verify(inboundGpSystemService).publishToSupplierQueue(any(Parameters.class));
    }

    @Test
    public void whenDuplicateMessageErrorOnInboundStateSave_thenNotPublishedToSupplierQueue() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setContent(exampleMessage);

        when(inboundStateRepository.save(any())).thenThrow(DuplicateKeyException.class);

        registrationConsumerService.handleRegistration(meshMessage);

        verifyNoInteractions(inboundGpSystemService);
    }
}
