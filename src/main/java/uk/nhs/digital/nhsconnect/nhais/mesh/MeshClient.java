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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshClient {

    private final MeshConfig meshConfig;

    private final MeshRequests meshRequests;

    @SneakyThrows
    public MeshMessageId sendEdifactMessage(String messageContent, String recipient) {
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            var request = meshRequests.sendMessage(recipient);
            request.setEntity(new StringEntity(messageContent));
            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.ACCEPTED.value()) {
                    throw new MeshApiConnectionException("Couldn't send MESH message.",
                        HttpStatus.ACCEPTED,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
                return parseInto(MeshMessageId.class, response);
            }
        }
    }

    @SneakyThrows
    public String getEdifactMessage(String messageId) {
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            try (CloseableHttpResponse response = client.execute(meshRequests.getMessage(messageId))) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    throw new MeshApiConnectionException("Couldn't download MESH message using id: " + messageId,
                        HttpStatus.OK,
                        HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
                }
                return EntityUtils.toString(response.getEntity());
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
                return Arrays.asList(parseInto(MeshMessages.class, response).getMessageIDs());
            }
        }
    }

    private <T> T parseInto(Class<T> clazz, CloseableHttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.reader().createParser(EntityUtils.toString(response.getEntity()));
        return objectMapper.readValue(parser, clazz);
    }

    private MeshApiConnectionException sendMessageError(CloseableHttpResponse response) throws IOException {
        return new MeshApiConnectionException("Couldn't send MESH message. Expected status code: " + HttpStatus.ACCEPTED.value() + ", but received: " + response.getStatusLine().getStatusCode());
    }
}
