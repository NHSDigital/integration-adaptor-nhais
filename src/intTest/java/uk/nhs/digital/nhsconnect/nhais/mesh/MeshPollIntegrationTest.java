package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public class MeshPollIntegrationTest {

    @Autowired
    private MeshConfig meshConfig;

    @Autowired
    private MeshClient meshClient;

    /**
     * This test by default calls fake-mesh, but if MESH credentials are passed as env variables it uses them to connect
     *
     * @throws Exception
     */
    @Test
    void When_PollingFromMesh_Then_ListIsReturned() {
        assertThat(meshClient.getInboxMessageIds()).isEqualTo(List.of());
    }
}