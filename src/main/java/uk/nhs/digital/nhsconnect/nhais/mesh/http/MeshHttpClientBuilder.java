package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import com.heroku.sdk.EnvKeyStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHttpClientBuilder {

    private final MeshConfig meshConfig;

    public CloseableHttpClient build() {
        return build(defaultSSLContext());
    }

    public CloseableHttpClient build(SSLContext sslContext) {
        NoopHostnameVerifier hostnameVerifier = new NoopHostnameVerifier(); //TODO: NoopHostnameVerifier works for fake-mesh - in production DefaultHostnameVerifier should be used
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    @SneakyThrows
    private SSLContext defaultSSLContext() {
        KeyStore ks = EnvKeyStore.createFromPEMStrings(meshConfig.getEndpointPrivateKey(), meshConfig.getEndpointCert(), meshConfig.getMailboxPassword()).keyStore();
        return SSLContexts.custom().loadKeyMaterial(ks, this.meshConfig.getMailboxPassword().toCharArray())
            .loadTrustMaterial(TrustAllStrategy.INSTANCE) //TODO: TrustAllStrategy works for fake mesh - in production TrustSelfSignedStrategy should be used
            .build();
    }

}
