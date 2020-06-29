package uk.nhs.digital.nhsconnect.nhais.mesh;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

    private final MeshHeaders meshHeaders;

    @SneakyThrows
    public MeshMessageId sendEdifactMessage(String messageContent, String recipient) {
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            var request = new HttpPost(meshConfig.getHost() + meshConfig.getMailboxId() + "/outbox/");
            request.setHeaders(meshHeaders.createSendHeaders(recipient));
            request.setEntity(new StringEntity(messageContent));
            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.ACCEPTED.value()) {
                    throw sendMessageError(response);
                }
                return parseInto(MeshMessageId.class, response);
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

    @SneakyThrows
    public List<String> getInboxMessageIds() {
        try (CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            HttpGet httpGet = new HttpGet(meshConfig.getHost() + meshConfig.getMailboxId() + "/inbox");
            httpGet.setHeaders(new MeshHeaders(meshConfig).createMinimalHeaders());
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                return Arrays.asList(parseInto(MeshMessages.class, response).getMessageIDs());
            }
        }
    }

    // TODO: stub, to be replaced by NIAD-266
    public String getMessage(String messageId) {
        return "the message";
    }
}
