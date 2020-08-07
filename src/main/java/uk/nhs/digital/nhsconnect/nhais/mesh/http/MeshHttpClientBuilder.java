package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import com.heroku.sdk.EnvKeyStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.util.Arrays;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHttpClientBuilder {

    private final MeshConfig meshConfig;

    public CloseableHttpClient build() {
        return build(defaultSSLContext());
    }

    public CloseableHttpClient build(SSLContext sslContext) {
//        NoopHostnameVerifier hostnameVerifier = new NoopHostnameVerifier(); //TODO: NoopHostnameVerifier works for fake-mesh - in production DefaultHostnameVerifier should be used
        PublicSuffixMatcher publicSuffixMatcher = new PublicSuffixMatcher(DomainType.UNKNOWN, Arrays.asList("gov.uk"), null);
        DefaultHostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, defaultHostnameVerifier);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    @SneakyThrows
    private SSLContext defaultSSLContext() {
        KeyStore ks = EnvKeyStore.createFromPEMStrings(meshConfig.getEndpointPrivateKey(), meshConfig.getEndpointCert(), meshConfig.getMailboxPassword()).keyStore();

        // subCA certificate + Root CA certificate
        KeyStore ts = EnvKeyStore.createFromPEMStrings(meshConfig.getSubCAcert(), meshConfig.getRootCA(), meshConfig.getMailboxPassword()).keyStore();
        return SSLContexts.custom()
            .loadKeyMaterial(ks, this.meshConfig.getMailboxPassword().toCharArray())
//            .loadTrustMaterial(TrustAllStrategy.INSTANCE) //TODO: TrustAllStrategy works for fake mesh - in production TrustSelfSignedStrategy should be used
            .loadTrustMaterial(ts, TrustSelfSignedStrategy.INSTANCE)
            .build();
    }

}
