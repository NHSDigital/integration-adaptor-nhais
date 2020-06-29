package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void when_CallingMeshSendMessageEndpoint_Then_MessageIdIsReturned() throws Exception {
        MeshMessageId meshMessageId = meshClient.sendEdifactMessage("edifact\nmessage", "recipient");
        assertThat(meshMessageId).isNotNull();
        assertThat(meshMessageId.getMessageID()).isNotEmpty();
        LOGGER.info(meshMessageId.getMessageID());
    }

}