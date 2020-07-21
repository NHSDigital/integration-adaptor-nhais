package uk.nhs.digital.nhsconnect.nhais.mesh;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshClient {

    private final MeshConfig meshConfig;
    private final MeshRequests meshRequests;
    private final MeshCypherDecoder meshCypherDecoder;

    @SneakyThrows
    public MeshMessageId sendEdifactMessage(OutboundMeshMessage outboundMeshMessage) {
        String recipientMailbox = meshCypherDecoder.getRecipientMailbox(outboundMeshMessage);
        LOGGER.debug("Sending EDIFACT message - recipient: {} MESH mailbox: {}", outboundMeshMessage.getHaTradingPartnerCode(), recipientMailbox);
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            var request = meshRequests.sendMessage(recipientMailbox, outboundMeshMessage.getWorkflowId());
            request.setEntity(new StringEntity(outboundMeshMessage.getContent()));
            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.ACCEPTED.value()) {
                    throw new MeshApiConnectionException("Couldn't send MESH message.",
                        HttpStatus.ACCEPTED,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
                MeshMessageId meshMessageId = parseInto(MeshMessageId.class, response);
                LOGGER.debug("Successfully sent MESH message to mailbox {}. Message id: {}", recipientMailbox, meshMessageId.getMessageID());
                return meshMessageId;
            }
        }
    }

    @SneakyThrows
    public InboundMeshMessage getEdifactMessage(String messageId) {
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            try (CloseableHttpResponse response = client.execute(meshRequests.getMessage(messageId))) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    throw new MeshApiConnectionException("Couldn't download MESH message using id: " + messageId,
                        HttpStatus.OK,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
                var meshMessage = new MeshMessage();
                meshMessage.setContent(EntityUtils.toString(response.getEntity()));
                meshMessage.setWorkflowId(WorkflowId.fromString(response.getHeaders("Mex-WorkflowID")[0].getValue()));
                meshMessage.setMeshMessageId(messageId);
                return meshMessage;
            }
        }
    }

    @SneakyThrows
    public void acknowledgeMessage(String messageId) {
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            try (CloseableHttpResponse response = client.execute(meshRequests.acknowledge(messageId))) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    throw new MeshApiConnectionException("Couldn't acknowledge MESH message using id: " + messageId,
                        HttpStatus.OK,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
            }
        }
    }

    @SneakyThrows
    public List<String> getInboxMessageIds() {
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            try (CloseableHttpResponse response = client.execute(meshRequests.getMessageIds())) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    throw new MeshApiConnectionException("Couldn't receive MESH message list",
                            HttpStatus.OK,
                            HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
                return Arrays.asList(parseInto(MeshMessages.class, response).getMessageIDs());
            }
        }
    }

    private <T> T parseInto(Class<T> clazz, CloseableHttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.reader().createParser(EntityUtils.toString(response.getEntity()));
        return objectMapper.readValue(parser, clazz);
    }
}
