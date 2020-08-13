package uk.nhs.digital.nhsconnect.nhais.inbound.queue;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InboundGpSystemServiceTest {

    @Mock
    private ObjectSerializer serializer;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Session session;

    @Mock
    private TextMessage textMessage;

    @InjectMocks
    private InboundGpSystemService inboundGpSystemService;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    private String gpSystemInboundQueueName;

    @Test
    public void testPublishToGpSupplierQueue() throws JMSException {
        Parameters parameters = new Parameters();
        String operationId = "123";
        ReferenceTransactionType.TransactionType transactionType = ReferenceTransactionType.Outbound.ACCEPTANCE;

        var dataToSend = new InboundGpSystemService.DataToSend()
            .setOperationId(operationId)
            .setTransactionType(transactionType)
            .setContent(parameters);

        String serializedData = "some_serialized_data";
        when(serializer.serialize(parameters)).thenReturn(serializedData);

        inboundGpSystemService.publishToSupplierQueue(dataToSend);

        var messageCreatorArgumentCaptor = ArgumentCaptor.forClass(MessageCreator.class);

        verify(jmsTemplate).send(eq(gpSystemInboundQueueName), messageCreatorArgumentCaptor.capture());

        when(session.createTextMessage(serializedData)).thenReturn(textMessage);

        messageCreatorArgumentCaptor.getValue().createMessage(session);

        verify(session).createTextMessage(eq(serializedData));
        verify(textMessage).setStringProperty(eq("OperationId"), eq(operationId));
        verify(textMessage).setStringProperty(eq("TransactionType"), eq(transactionType.name().toLowerCase()));
    }
}
