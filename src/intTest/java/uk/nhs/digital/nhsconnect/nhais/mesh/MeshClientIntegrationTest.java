package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.mesh.token.MeshAuthorizationToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heroku.sdk.EnvKeyStore;

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