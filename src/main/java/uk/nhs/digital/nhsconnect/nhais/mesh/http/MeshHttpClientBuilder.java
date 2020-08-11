package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHttpClientBuilder {
    private final MeshConfig meshConfig;
    private final SSLContext sslContext;

    public CloseableHttpClient build() {
        if (Boolean.parseBoolean(meshConfig.getCertValidation())) {
            return buildDefaultHttpClient();
        } else {
            return buildNoCertValidationClient();
        }
    }

    private CloseableHttpClient buildDefaultHttpClient() {
        PublicSuffixMatcher publicSuffixMatcher = new PublicSuffixMatcher(
            DomainType.UNKNOWN,
            Collections.singletonList(meshConfig.getPublicSuffix()),
            null);
        DefaultHostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, defaultHostnameVerifier);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    private CloseableHttpClient buildNoCertValidationClient() {
        NoopHostnameVerifier hostnameVerifier = new NoopHostnameVerifier();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }
}
