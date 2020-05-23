package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InboundGpSystemServiceTest {

    @Mock
    FhirParser fhirParser;

    @Mock
    AmqpTemplate rabbitTemplate;

    @Value("${nhais.amqp.exchange}")
    private String exchange;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    private String gpSystemInboundQueueName;

    @InjectMocks
    InboundGpSystemService inboundGpSystemService;

    @Test
    public void testPublishToGpSupplierQueue() {
        Parameters parameters = new Parameters();
        String jsonEncodedFhir = "{\"resourceType\":\"Parameters\"}";
        when(fhirParser.encodeToString(parameters)).thenReturn(jsonEncodedFhir);

        inboundGpSystemService.publishToSupplierQueue(parameters);

        verify(rabbitTemplate).send(eq(exchange), eq(gpSystemInboundQueueName), argThat(message ->
            new String(message.getBody()).equals(jsonEncodedFhir)
        ));
    }

}
