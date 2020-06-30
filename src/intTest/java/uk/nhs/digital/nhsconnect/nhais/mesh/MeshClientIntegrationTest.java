package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public class MeshClientIntegrationTest {

    @Autowired
    private MeshClient meshClient;

    @Autowired
    private MeshConfig meshConfig;

    @Test
    void when_CallingMeshSendMessageEndpoint_Then_MessageIdIsReturned(){
        MeshMessageId meshMessageId = meshClient.sendEdifactMessage("edifact\nmessage", "recipient");
        assertThat(meshMessageId).isNotNull();
        assertThat(meshMessageId.getMessageID()).isNotEmpty();
        LOGGER.info(meshMessageId.getMessageID());
    }

    @Test
    void when_CallingMeshGetMessageEndpoint_Then_MessageIsReturned() {
        String messageContent = "test_message";
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(messageContent, meshConfig.getMailboxId());

        String edifactMessage = meshClient.getEdifactMessage(testMessageId.getMessageID());
        assertThat(edifactMessage).isEqualTo(messageContent);
        LOGGER.info(edifactMessage);
    }

    @Test
    void when_CallingMeshAcknowledgeEndpoint_Then_NoExceptionIsThrown() {
        String messageContent = "test_message";
        MeshMessageId testMessageId = meshClient.sendEdifactMessage(messageContent, meshConfig.getMailboxId());

        assertThatCode(() -> meshClient.acknowledgeMessage(testMessageId.getMessageID()))
                .doesNotThrowAnyException();
    }

}