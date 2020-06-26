package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public class MeshClientIntegrationTest {

    private MeshClient meshClient;

    @Value("classpath:fake-mesh/fakemesh.ca.cert.pem")
    private Resource fakeMeshCert;

    @Value("classpath:fake-mesh/fakemesh.ca.key.pem")
    private Resource fakeMeshKey;

    private FakeMeshConfig meshConfig;

    @BeforeEach
    void setUp() throws Exception{
        System.setProperty("NHAIS_MESH_ENDPOINT_CERT", new String(Files.readAllBytes(fakeMeshCert.getFile().toPath())));
        System.setProperty("NHAIS_MESH_ENDPOINT_KEY", new String(Files.readAllBytes(fakeMeshKey.getFile().toPath())));
        meshConfig = new FakeMeshConfig();
        meshClient = new MeshClient(meshConfig, new MeshHeaders(meshConfig));
    }

    @Test
    void when_CallingMeshGetMessageEndpoint_Then_MessageIsReturned() throws Exception {
        MeshMessageId meshMessageId = meshClient.sendEdifactMessage("edifact\nmessage", meshConfig.getMailboxId());
        assertThat(meshMessageId).isNotNull();
        assertThat(meshMessageId.getMessageID()).isNotEmpty();
        LOGGER.info(meshMessageId.getMessageID());
    }

}