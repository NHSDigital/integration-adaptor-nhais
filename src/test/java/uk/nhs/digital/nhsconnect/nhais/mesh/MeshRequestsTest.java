package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshHeaders;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshRequests;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

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
    void When_SendingRegistrationMessage_Then_ExpectHttpPostAndCorrectUri() {
        MeshRequests meshRequests = new MeshRequests(meshConfig, meshHeaders);

        String recipient = "recipient";
        var request = meshRequests.sendMessage(recipient, WorkflowId.REGISTRATION);

        assertSending(request, recipient, WorkflowId.REGISTRATION);
    }

    @Test
    void When_SendingRecepMessage_Then_ExpectHttpPostAndCorrectUri() {
        MeshRequests meshRequests = new MeshRequests(meshConfig, meshHeaders);

        String recipient = "recipient";
        var request = meshRequests.sendMessage(recipient, WorkflowId.RECEP);

        assertSending(request, recipient, WorkflowId.RECEP);
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

    private void assertSending(HttpEntityEnclosingRequestBase request, String recipient, WorkflowId workflowId) {
        assertThat(request).isExactlyInstanceOf(HttpPost.class);
        assertThat(request.getURI().toString()).isEqualTo("https://localhost:8829/messageexchange/mailboxId/outbox");
        Header[] mexToHeader = request.getHeaders("Mex-To");
        assertThat(mexToHeader.length).isEqualTo(1);
        assertThat(mexToHeader[0].getValue()).isEqualTo(recipient);
        assertThat(request.getHeaders("Mex-WorkflowID")[0].getValue()).isEqualTo(workflowId.getWorkflowId());
    }
}