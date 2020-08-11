package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshHeaders;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshHttpClientBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
@DirtiesContext
public class MeshConnectivityIntegrationTest {

    @Autowired
    private MeshConfig meshConfig;

    @Autowired
    private MeshHttpClientBuilder meshHttpClientBuilder;

    /**
     * This test by default calls fake-mesh, but if MESH credentials are passed as env variables it uses them to connect
     *
     * @throws Exception
     */
    @Test
    void when_CallingMeshCountMessagesEndpoint_Then_Http200IsReturned() throws Exception {
        try (CloseableHttpClient client = meshHttpClientBuilder.build()) {
            HttpGet httpGet = new HttpGet(meshConfig.getHost() + meshConfig.getMailboxId() + "/count");
            httpGet.setHeaders(new MeshHeaders(meshConfig).createMinimalHeaders());
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());
            }
        }
    }

}