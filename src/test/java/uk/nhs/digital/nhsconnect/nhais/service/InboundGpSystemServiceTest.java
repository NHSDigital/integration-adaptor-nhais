package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InboundGpSystemServiceTest {

    @Mock
    FhirParser fhirParser;

    @Mock
    JmsTemplate jmsTemplate;

    @Mock
    Message message;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    private String gpSystemInboundQueueName;

    @InjectMocks
    InboundGpSystemService inboundGpSystemService;

    @Test
    public void testPublishToGpSupplierQueue() throws JMSException {
        Parameters parameters = new Parameters();
        String operationId = "123";
        String jsonEncodedFhir = "{\"resourceType\":\"Parameters\"}";
        when(fhirParser.encodeToString(parameters)).thenReturn(jsonEncodedFhir);

        inboundGpSystemService.publishToSupplierQueue(parameters, operationId);

        var messagePostProcessorArgumentCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        verify(jmsTemplate).convertAndSend(eq(gpSystemInboundQueueName), eq(jsonEncodedFhir), messagePostProcessorArgumentCaptor.capture());

        messagePostProcessorArgumentCaptor.getValue().postProcessMessage(message);

        verify(message).setStringProperty("OperationId", operationId);
        verifyNoMoreInteractions(message);
    }
}
