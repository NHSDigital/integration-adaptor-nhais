package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import com.heroku.sdk.EnvKeyStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
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
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHttpClientBuilder {
    private static final String KEY_MANAGER_FACTORY_TYPE = "Sunx509";

    private final MeshConfig meshConfig;

    public CloseableHttpClient build() {
        if (Boolean.parseBoolean(meshConfig.getCertValidation())) {
            return buildDefaultHttpClient(defaultSSLContext());
        } else {
            LOGGER.warn("Using SSL without cert validation!");
            return buildNoCertValidationClient(noValidationSSLContext());
        }
    }

    private CloseableHttpClient buildDefaultHttpClient(SSLContext sslContext) {
        PublicSuffixMatcher publicSuffixMatcher = new PublicSuffixMatcher(
            DomainType.UNKNOWN,
            Collections.singletonList(meshConfig.getPublicSuffix()),
            null);
        DefaultHostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, defaultHostnameVerifier);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    private CloseableHttpClient buildNoCertValidationClient(SSLContext sslContext) {
        NoopHostnameVerifier hostnameVerifier = new NoopHostnameVerifier();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    @SneakyThrows
    private SSLContext defaultSSLContext() {
        KeyStore ks = buildKeyStore();
        KeyStore ts = buildTrustStore();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_TYPE);
        keyManagerFactory.init(ks, meshConfig.getMailboxPassword().toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);

        SSLContext sslContext = SSLContexts.custom()
            .loadKeyMaterial(ks, this.meshConfig.getMailboxPassword().toCharArray())
            .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
            .build();

        sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return sslContext;
    }

    @SneakyThrows
    private SSLContext noValidationSSLContext() {
        return SSLContexts.custom()
            .loadKeyMaterial(buildKeyStore(), this.meshConfig.getMailboxPassword().toCharArray())
            .loadTrustMaterial(TrustAllStrategy.INSTANCE)
            .build();
    }

    @SneakyThrows
    private KeyStore buildKeyStore() {
        return EnvKeyStore.createFromPEMStrings(
            meshConfig.getEndpointPrivateKey(),
            meshConfig.getEndpointCert(),
            meshConfig.getMailboxPassword()).keyStore();
    }

    @SneakyThrows
    private KeyStore buildTrustStore() {
        return EnvKeyStore.createFromPEMStrings(
            meshConfig.getSubCAcert(),
            meshConfig.getMailboxPassword()).keyStore();
    }
}
