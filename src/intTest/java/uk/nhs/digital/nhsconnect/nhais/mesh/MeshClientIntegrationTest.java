package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
@DirtiesContext
public class MeshClientIntegrationTest {

    @Autowired
    private MeshClient meshClient;

    @Autowired
    private MeshConfig meshConfig;

    @AfterEach
    void tearDown() {
        List<String> inboxMessageIds = meshClient.getInboxMessageIds();
        inboxMessageIds.forEach( messageId -> meshClient.acknowledgeMessage(messageId));
    }

    @Test
    void when_CallingMeshSendMessageEndpoint_Then_MessageIdIsReturned() {
        MeshMessageId meshMessageId = meshClient.sendEdifactMessage("edifact\nmessage", "recipient");
        assertThat(meshMessageId).isNotNull();
        assertThat(meshMessageId.getMessageID()).isNotEmpty();
    }

    @Test
    void when_CallingMeshGetMessageEndpoint_Then_MessageIsReturned() {
        String messageContent = "test_message";
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(messageContent, meshConfig.getMailboxId());

        MeshMessage meshMessage = meshClient.getEdifactMessage(testMessageId.getMessageID());
        assertThat(meshMessage.getContent()).isEqualTo(messageContent);
        assertThat(meshMessage.getWorkflowId()).isEqualTo(WorkflowId.REGISTRATION);
    }

    @Test
    void when_CallingMeshAcknowledgeEndpoint_Then_NoExceptionIsThrown() {
        String messageContent = "test_message";
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(messageContent, meshConfig.getMailboxId());

        assertThatCode(() -> meshClient.acknowledgeMessage(testMessageId.getMessageID()))
            .doesNotThrowAnyException();
    }

    @Test
    void When_PollingFromMesh_Then_EmptyListIsReturned() {
        assertThat(meshClient.getInboxMessageIds()).isEqualTo(List.of());
    }

    @Test
    void When_PollingFromMeshAfterSendingMsg_Then_ListWithMsgIdIsReturned() {
        String messageContent = "test_message";
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(messageContent, meshConfig.getMailboxId());

        assertThat(meshClient.getInboxMessageIds()).contains(testMessageId.getMessageID());
    }
}