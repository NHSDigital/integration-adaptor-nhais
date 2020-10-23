package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.RecipientMailboxIdMappings;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessageId;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessages;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshClient {

    private final MeshRequests meshRequests;
    private final RecipientMailboxIdMappings recipientMailboxIdMappings;
    private final MeshHttpClientBuilder meshHttpClientBuilder;

    @SneakyThrows
    public void authenticate() {
        try (CloseableHttpClient client = meshHttpClientBuilder.build()) {
            final var loggingName = "Authenticate";
            var request = meshRequests.authenticate();
            logRequest(loggingName, request);
            try (CloseableHttpResponse response = client.execute(request)) {
                logResponse(loggingName, response);
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    throw new MeshApiConnectionException("Couldn't authenticate to MESH.",
                        HttpStatus.OK,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
            }
        }
    }

    @SneakyThrows
    public MeshMessageId sendEdifactMessage(OutboundMeshMessage outboundMeshMessage) {
        final var loggingName = "Send a message";
        String recipientMailbox = recipientMailboxIdMappings.getRecipientMailboxId(outboundMeshMessage);
        LOGGER.info("Sending to MESH API: recipient: {}, MESH mailbox: {}, workflow: {}", outboundMeshMessage.getHaTradingPartnerCode(), recipientMailbox, outboundMeshMessage.getWorkflowId());
        try (CloseableHttpClient client = meshHttpClientBuilder.build()) {
            var request = meshRequests.sendMessage(recipientMailbox, outboundMeshMessage.getWorkflowId());
            String contentString = outboundMeshMessage.getContent();
            byte[] contentBytes = contentString.getBytes(StandardCharsets.UTF_8);
            request.setEntity(new ByteArrayEntity(contentBytes));
            logRequest(loggingName, request);
            try (CloseableHttpResponse response = client.execute(request)) {
                logResponse(loggingName, response);
                if (response.getStatusLine().getStatusCode() != HttpStatus.ACCEPTED.value()) {
                    String content = EntityUtils.toString(response.getEntity()); // safe to get content in case of error
                    throw new MeshApiConnectionException("Couldn't send MESH message.",
                        HttpStatus.ACCEPTED,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()),
                        content);
                }
                MeshMessageId meshMessageId = parseInto(MeshMessageId.class, response, loggingName);
                LOGGER.info("Successfully sent transaction OperationId={} to MeshMailboxId={} in MeshMessageId={}",
                    outboundMeshMessage.getOperationId(), recipientMailbox, meshMessageId.getMessageID());
                return meshMessageId;
            }
        }
    }

    @SneakyThrows
    public InboundMeshMessage getEdifactMessage(String messageId) {
        final var loggingName = "Download message";
        try (CloseableHttpClient client = meshHttpClientBuilder.build()) {
            var request = meshRequests.getMessage(messageId);
            logRequest(loggingName, request);
            try (CloseableHttpResponse response = client.execute(request)) {
                logResponse(loggingName, response);
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    String content = EntityUtils.toString(response.getEntity()); // safe to get content in case of error
                    throw new MeshApiConnectionException("Couldn't download MeshMessageId=" + messageId,
                        HttpStatus.OK,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()),
                        content);
                }
                var meshMessage = new MeshMessage();
                /* Get the workflowId before extracting the message body. An exception is thrown if the workflowId is
                unsupported causing that message to be skipped. Messages for unsupported workflows might be very large
                (up to 100mb) so we want to avoid reading the content into a String if not needed. */
                meshMessage.setWorkflowId(WorkflowId.fromString(response.getHeaders("Mex-WorkflowID")[0].getValue()));
                meshMessage.setContent(EntityUtils.toString(response.getEntity()));
                LOGGER.debug("MESH '{}' response content: {}", loggingName, meshMessage.getContent());
                meshMessage.setMeshMessageId(messageId);
                return meshMessage;
            }
        }
    }

    @SneakyThrows
    public void acknowledgeMessage(String messageId) {
        final var loggingName = "Acknowledge message";
        try (CloseableHttpClient client = meshHttpClientBuilder.build()) {
            var request = meshRequests.acknowledge(messageId);
            logRequest(loggingName, request);
            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    logResponse(loggingName, response);
                    throw new MeshApiConnectionException("Couldn't acknowledge MESH message using id: " + messageId,
                        HttpStatus.OK,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
            }
        }
    }

    @SneakyThrows
    public List<String> getInboxMessageIds() {
        final var loggingName = "Check inbox";
        try (CloseableHttpClient client = meshHttpClientBuilder.build()) {
            var request = meshRequests.getMessageIds();
            logRequest(loggingName, request);
            try (CloseableHttpResponse response = client.execute(request)) {
                logResponse(loggingName, response);
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    throw new MeshApiConnectionException("Couldn't receive MESH message list",
                        HttpStatus.OK,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
                var meshMessages = parseInto(MeshMessages.class, response, loggingName);
                return Arrays.asList(meshMessages.getMessageIDs());
            }
        }
    }

    private <T> T parseInto(Class<T> clazz, CloseableHttpResponse response, String loggingName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        var content = EntityUtils.toString(response.getEntity());
        LOGGER.debug("MESH '{}' response content: {}", loggingName, content);
        return objectMapper.readValue(content, clazz);
    }

    @SneakyThrows
    private void logRequest(String type, HttpRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MESH '{}' request line: {}", type, request.getRequestLine());
            LOGGER.debug("MESH '{}' request headers: {}", type, request.getAllHeaders());

            if (request instanceof HttpEntityEnclosingRequest) {
                var entity = ((HttpEntityEnclosingRequest) request).getEntity();
                if (entity != null) {
                    LOGGER.debug("MESH '{}' request content line: {}", type, entity);
                    // request content is usually not "repeatable" so we can only decode it once. Log response content separately.
                }
            }
        }
    }

    @SneakyThrows
    private void logResponse(String type, HttpResponse response) {
        // log as INFO - these are useful for normal operation to trace requests and error reports
        LOGGER.info("MESH '{}' response status line: {}", type, response.getStatusLine());
        LOGGER.info("MESH '{}' response headers: {}", type, response.getAllHeaders());
        if (LOGGER.isDebugEnabled()) {
            if (response.getEntity() != null) {
                var entity = response.getEntity();
                LOGGER.debug("MESH '{}' response content encoding: {}, content type: {}, content length: {}", type, entity.getContentEncoding(), entity.getContentType(), entity.getContentLength());
                // response is usually not "repeatable" so we can only decode it once. Log response content separately.
            }
        }
    }
}
