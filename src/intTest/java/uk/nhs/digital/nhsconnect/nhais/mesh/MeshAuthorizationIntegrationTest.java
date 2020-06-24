package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.mesh.token.MeshAuthorizationToken;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heroku.sdk.EnvKeyStore;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public class MeshAuthorizationIntegrationTest {

    @Autowired
    private MeshConfig meshConfig;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * This test is ignored by default.
     * It needs OpenTest VPN active and MESH configuration environment variables set
     * @throws Exception
     */
    @Test
    @Disabled("Used locally be developers to prove MESH API connectivity")
    void whenCallingMeshAuthorizationEndpointThenMailboxIdIsReturned() throws Exception {
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(getSSLContext(),
            new DefaultHostnameVerifier());
        try(CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(factory).build()) {
            HttpPost httpPost = new HttpPost(meshConfig.getHost() + meshConfig.getMailboxId());
            httpPost.addHeader("Mex-ClientVersion", "1.0");
            httpPost.addHeader("Mex-OSVersion", "1.0");
            httpPost.addHeader("Mex-OSName", "Unix");
            httpPost.addHeader("Authorization", new MeshAuthorizationToken(meshConfig).getValue());
            try(CloseableHttpResponse response = client.execute(httpPost)){
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());
                JsonParser parser = objectMapper.reader().createParser(EntityUtils.toString(response.getEntity()));
                MeshAuthorizationResponse meshAuthorizationResponse = objectMapper.readValue(parser, MeshAuthorizationResponse.class);
                assertThat(meshAuthorizationResponse.mailboxId).isEqualTo(meshConfig.getMailboxId());
            }
        }

    }

    @SneakyThrows
    private SSLContext getSSLContext() {
        KeyStore ks = EnvKeyStore.create("NHAIS_MESH_ENDPOINT_PRIVATE_KEY", "NHAIS_MESH_ENDPOINT_CERT", "NHAIS_MESH_MAILBOX_PASSWORD").keyStore();
        return SSLContexts.custom().loadKeyMaterial(ks, meshConfig.getMailboxPassword().toCharArray())
            .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
            .build();
    }

    @Data
    private static class MeshAuthorizationResponse {
        private String mailboxId;
    }
}