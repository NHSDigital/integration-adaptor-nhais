package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public class MeshAuthorizationIntegrationTest {

    @Autowired
    private MeshConfig meshConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:fake-mesh/fakemesh.ca.cert.pem")
    private Resource fakeMeshCert;

    @Value("classpath:fake-mesh/fakemesh.ca.key.pem")
    private Resource fakeMeshKey;

    private FakeMeshConfig fakeMeshConfig;

    @BeforeEach
    void setUp() throws Exception{
        System.setProperty("NHAIS_MESH_ENDPOINT_CERT", new String(Files.readAllBytes(fakeMeshCert.getFile().toPath())));
        System.setProperty("NHAIS_MESH_ENDPOINT_KEY", new String(Files.readAllBytes(fakeMeshKey.getFile().toPath())));
        fakeMeshConfig = new FakeMeshConfig();
    }

    /**
     * This test is ignored by default.
     * It needs OpenTest VPN active and MESH configuration environment variables set
     * @throws Exception
     */
    @Test
    @Disabled("Used locally be developers to prove OpenTest MESH API connectivity")
    void when_CallingMeshAuthorizationEndpoint_Then_MailboxIdIsReturned() throws Exception {
        try(CloseableHttpClient client = new MeshHttpClientBuilder(meshConfig).build()) {
            HttpPost httpPost = new HttpPost(meshConfig.getHost() + meshConfig.getMailboxId());
            httpPost.setHeaders(new MeshHeaders(meshConfig).createMinimalHeaders());
            try(CloseableHttpResponse response = client.execute(httpPost)){
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());
                JsonParser parser = objectMapper.reader().createParser(EntityUtils.toString(response.getEntity()));
                MeshAuthorizationResponse meshAuthorizationResponse = objectMapper.readValue(parser, MeshAuthorizationResponse.class);
                assertThat(meshAuthorizationResponse.mailboxId).isEqualTo(meshConfig.getMailboxId());
            }
        }

    }

    @Test
    void when_CallingFakeMeshCountMessagesEndpoint_Then_Http200IsReturned() throws Exception {
        try(CloseableHttpClient client = new MeshHttpClientBuilder(fakeMeshConfig).build()) {
            HttpGet httpGet = new HttpGet(fakeMeshConfig.getHost() + fakeMeshConfig.getMailboxId()+"/count");
            httpGet.setHeaders(new MeshHeaders(fakeMeshConfig).createMinimalHeaders());
            try(CloseableHttpResponse response = client.execute(httpGet)){
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());
            }
        }
    }

    @Data
    private static class MeshAuthorizationResponse {
        private String mailboxId;
    }
}