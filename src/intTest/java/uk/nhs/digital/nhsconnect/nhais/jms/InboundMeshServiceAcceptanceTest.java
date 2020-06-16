package uk.nhs.digital.nhsconnect.nhais.jms;

import lombok.SneakyThrows;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class InboundMeshServiceAcceptanceTest extends InboundMeshServiceBaseTest {

    @Autowired
    private FhirParser fhirParser;

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.class)
    void test(String category, String edifactInput, String fhirOutput) throws JMSException {
        sendToMeshInboundQueue(edifactInput);

        var expectedTransactionType = category.split("/")[0];

        var gpSystemInboundQueueMessage = getGpSystemInboundQueueMessage();

        assertMessageHeaders(gpSystemInboundQueueMessage, expectedTransactionType);
        assertMessageBody(gpSystemInboundQueueMessage, fhirOutput);
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

    private void sendToMeshInboundQueue(String edifact) {
        var meshMessage = new MeshMessage()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setContent(edifact);

        sendToMeshInboundQueue(meshMessage);
    }

    static class CustomArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            var resources = getResources();

            var grouped = Arrays.stream(resources)
                .collect(Collectors.groupingBy(resource -> {
                    var pathParts = ((FileSystemResource) resource).getPath().split("/");
                    var category = pathParts[pathParts.length - 2];
                    var fileName = pathParts[pathParts.length - 1];
                    var fileNumber = fileName.split("\\.")[0];
                    return category + "/" + fileNumber;
                })).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    es -> {
                        if (es.getValue().size() != 2) {
                            throw new IllegalStateException("There should be 2 test data files: N.edifact.dat and N.fhir.json");
                        }
                        String edifactInput = readFile(es.getValue().get(0));
                        String fhirOutput = readFile(es.getValue().get(1));

                        return new String[]{edifactInput, fhirOutput};
                    }));

            return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(es -> Arguments.of(es.getKey(), es.getValue()[0], es.getValue()[1]));
        }

        @SneakyThrows
        private String readFile(Resource resource) {
            return new String(Files.readAllBytes(resource.getFile().toPath()));
        }

        private Resource[] getResources() throws IOException {
            ClassLoader cl = this.getClass().getClassLoader();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
            return resolver.getResources("classpath*:/inbound_acceptance_test_data/*/*");
        }
    }
}
