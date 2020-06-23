package uk.nhs.digital.nhsconnect.nhais.uat;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import uk.nhs.digital.nhsconnect.nhais.jms.MeshServiceBaseTest;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.assertj.core.api.Assertions.assertThat;

public class InboundMeshServiceUAT extends MeshServiceBaseTest {

    @Autowired
    private FhirParser fhirParser;

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.Inbound.class)
    void testTranslatingFromEdifactToFhir(String category, TestData testData) throws JMSException {
        // send EDIFACT to "inbound queue"
        sendToMeshInboundQueue(new MeshMessage().setWorkflowId(WorkflowId.REGISTRATION).setContent(testData.getEdifact()));

        var expectedTransactionType = category.split("/")[0];

        // fetch FHIR from "gp inbound queue"
        var gpSystemInboundQueueMessage = getGpSystemInboundQueueMessage();

        // assert transaction type in JMS header is correct
        assertMessageHeaders(gpSystemInboundQueueMessage, expectedTransactionType);
        // assert output FHIR is correct
        assertMessageBody(gpSystemInboundQueueMessage, testData.getFhir());
    }

    private void assertMessageBody(Message gpSystemInboundQueueMessage, String expectedFhir) throws JMSException {
        var resource = parseGpInboundQueueMessage(gpSystemInboundQueueMessage);
        assertThat(resource).isExactlyInstanceOf(Parameters.class);

        String fhir = fhirParser.encodeToString(resource);

        assertThat(fhir).isEqualTo(expectedFhir);
    }

    private void assertMessageHeaders(Message gpSystemInboundQueueMessage, String expectedTransactionType) throws JMSException {
        String transactionType = gpSystemInboundQueueMessage.getStringProperty(JmsHeaders.TRANSACTION_TYPE);
        assertThat(transactionType).isEqualTo(expectedTransactionType);
    }
}
