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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHttpClientBuilder {
    private static final String KEY_MANAGER_FACTORY_TYPE = "Sunx509";

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
        System.out.println("------------------------------------- default SSL CONTEXT");
        KeyStore ks = EnvKeyStore.createFromPEMStrings(meshConfig.getEndpointPrivateKey(), meshConfig.getEndpointCert(), meshConfig.getMailboxPassword()).keyStore();
        KeyStore ts = EnvKeyStore.createFromPEMStrings(meshConfig.getSubCAcert(), meshConfig.getMailboxPassword()).keyStore();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_TYPE);
        keyManagerFactory.init(ks, meshConfig.getMailboxPassword().toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);

        SSLContext sslContext = SSLContexts.custom()
            .loadKeyMaterial(ks, this.meshConfig.getMailboxPassword().toCharArray())
//            .loadTrustMaterial(TrustAllStrategy.INSTANCE) //TODO: TrustAllStrategy works for fake mesh - in production TrustSelfSignedStrategy should be used
            .loadTrustMaterial(ts, TrustSelfSignedStrategy.INSTANCE)
            .build();

        sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return sslContext;
    }

}
