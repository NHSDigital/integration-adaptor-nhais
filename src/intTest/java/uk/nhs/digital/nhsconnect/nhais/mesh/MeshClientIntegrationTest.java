package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.jms.MeshServiceBaseTest;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
@DirtiesContext
public class MeshClientIntegrationTest extends MeshServiceBaseTest {

    private static final String RECIPIENT = "XX11";
    private static final String CONTENT = "test_message";
    private static final OutboundMeshMessage OUTBOUND_MESH_MESSAGE = OutboundMeshMessage.create(
        RECIPIENT, WorkflowId.REGISTRATION, CONTENT, null, null
    );

    @AfterEach
    void tearDown() {
        clearMeshMailbox();
    }

    @Test
    void When_CallingMeshSendMessageEndpoint_Then_MessageIdIsReturned() {
        MeshMessageId meshMessageId = meshClient.sendEdifactMessage(OUTBOUND_MESH_MESSAGE);
        assertThat(meshMessageId).isNotNull();
        assertThat(meshMessageId.getMessageID()).isNotEmpty();
    }

    @Test
    void When_CallingMeshGetMessageEndpoint_Then_MessageIsReturned() {
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(OUTBOUND_MESH_MESSAGE);

        InboundMeshMessage meshMessage = meshClient.getEdifactMessage(testMessageId.getMessageID());
        assertThat(meshMessage.getContent()).isEqualTo(CONTENT);
        assertThat(meshMessage.getWorkflowId()).isEqualTo(WorkflowId.REGISTRATION);
    }

    @Test
    void When_CallingMeshAcknowledgeEndpoint_Then_NoExceptionIsThrown() {
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(OUTBOUND_MESH_MESSAGE);

        assertThatCode(() -> meshClient.acknowledgeMessage(testMessageId.getMessageID()))
            .doesNotThrowAnyException();
    }

    @Test
    void When_PollingFromMesh_Then_EmptyListIsReturned() {
        assertThat(meshClient.getInboxMessageIds()).isEqualTo(List.of());
    }

    @Test
    void When_PollingFromMeshAfterSendingMsg_Then_ListWithMsgIdIsReturned() {
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(OUTBOUND_MESH_MESSAGE);

        assertThat(meshClient.getInboxMessageIds()).contains(testMessageId.getMessageID());
    }
}