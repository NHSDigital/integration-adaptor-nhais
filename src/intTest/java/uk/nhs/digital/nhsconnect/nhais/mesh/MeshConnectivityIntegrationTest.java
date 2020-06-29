package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public class MeshConnectivityIntegrationTest {

    @Autowired
    private MeshConfig meshConfig;

    /**
     * This test by default calls fake-mesh, but if MESH credentials are passed as env variables it uses them to connect
     * @throws Exception
     */
    @Test
    void when_CallingMeshCountMessagesEndpoint_Then_Http200IsReturned() throws Exception {
        try(CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            HttpGet httpGet = new HttpGet(meshConfig.getHost() + meshConfig.getMailboxId()+"/count");
            httpGet.setHeaders(new MeshHeaders(meshConfig).createMinimalHeaders());
            try(CloseableHttpResponse response = client.execute(httpGet)){
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());
            }
        }
    }

}