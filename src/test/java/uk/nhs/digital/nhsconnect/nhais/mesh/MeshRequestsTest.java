package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeshRequestsTest {

    private final MeshConfig meshConfig = new MeshConfig("mailboxId",
        "password",
        "SharedKey",
        "https://localhost:8829/messageexchange/",
        StringUtils.EMPTY,
        StringUtils.EMPTY);
    private final MeshHeaders meshHeaders = new MeshHeaders(meshConfig);

    @Test
    void When_GettingMessage_Then_ExpectHttpGetAndCorrectUri() {
        MeshRequests meshRequests = new MeshRequests(meshConfig, meshHeaders);

        var request = meshRequests.getMessage("messageId");

        assertThat(request).isExactlyInstanceOf(HttpGet.class);
        assertThat(request.getURI().toString()).isEqualTo("https://localhost:8829/messageexchange/mailboxId/inbox/messageId");
    }

    @Test
    void When_SendingMessage_Then_ExpectHttpPostAndCorrectUri() {
        MeshRequests meshRequests = new MeshRequests(meshConfig, meshHeaders);

        String recipient = "recipient";
        var request = meshRequests.sendMessage(recipient);

        assertThat(request).isExactlyInstanceOf(HttpPost.class);
        assertThat(request.getURI().toString()).isEqualTo("https://localhost:8829/messageexchange/mailboxId/outbox/");
        Header[] mexToHeader = request.getHeaders("Mex-To");
        assertThat(mexToHeader.length).isEqualTo(1);
        assertThat(mexToHeader[0].getValue()).isEqualTo(recipient);
    }

    @Test
    void When_GettingMessageIds_Then_ExpectHttpGetAndCorrectUri() {
        MeshRequests meshRequests = new MeshRequests(meshConfig, meshHeaders);

        var request = meshRequests.getMessageIds();

        assertThat(request).isExactlyInstanceOf(HttpGet.class);
        assertThat(request.getURI().toString()).isEqualTo("https://localhost:8829/messageexchange/mailboxId/inbox");
    }

    @Test
    void When_AcknowledgeMessage_Then_ExpectHttpPutAndCorrectUri() {
        MeshRequests meshRequests = new MeshRequests(meshConfig, meshHeaders);

        var request = meshRequests.acknowledge("messageId");

        assertThat(request).isExactlyInstanceOf(HttpPut.class);
        assertThat(request.getURI().toString()).isEqualTo("https://localhost:8829/messageexchange/mailboxId/inbox/messageId/status/acknowledged");
    }

}