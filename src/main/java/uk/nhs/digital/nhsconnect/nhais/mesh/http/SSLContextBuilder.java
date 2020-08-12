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
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SSLContextBuilder {
    private static final String KEY_MANAGER_FACTORY_TYPE = "Sunx509";

    private final MeshConfig meshConfig;

    @Bean
    public SSLConnectionSocketFactory factory() {
        if (Boolean.parseBoolean(meshConfig.getCertValidation())) {
            return new SSLConnectionSocketFactory(defaultSSLContext(), new DefaultHostnameVerifier());
        } else {
            LOGGER.warn("Using SSL without cert validation!");
            return new SSLConnectionSocketFactory(noValidationSSLContext(), new NoopHostnameVerifier());
        }
    }

    @SneakyThrows
    private SSLContext defaultSSLContext() {
        KeyStore ks = buildKeyStore(meshConfig);
        KeyStore ts = buildTrustStore(meshConfig);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_TYPE);
        keyManagerFactory.init(ks, meshConfig.getMailboxPassword().toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);

        SSLContext sslContext = SSLContexts.custom()
            .loadKeyMaterial(ks, meshConfig.getMailboxPassword().toCharArray())
            .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
            .build();

        sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return sslContext;
    }

    @SneakyThrows
    private SSLContext noValidationSSLContext() {
        return SSLContexts.custom()
            .loadKeyMaterial(buildKeyStore(meshConfig), meshConfig.getMailboxPassword().toCharArray())
            .loadTrustMaterial(TrustAllStrategy.INSTANCE)
            .build();
    }

    @SneakyThrows
    private static KeyStore buildKeyStore(MeshConfig meshConfig) {
        return EnvKeyStore.createFromPEMStrings(
            meshConfig.getEndpointPrivateKey(),
            meshConfig.getEndpointCert(),
            meshConfig.getMailboxPassword()).keyStore();
    }

    @SneakyThrows
    private static KeyStore buildTrustStore(MeshConfig meshConfig) {
        return EnvKeyStore.createFromPEMStrings(
            meshConfig.getSubCAcert(),
            meshConfig.getMailboxPassword()).keyStore();
    }
}
