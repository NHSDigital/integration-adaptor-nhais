package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHttpClientBuilder {

    private final SSLConnectionSocketFactory sslFactory;

    public CloseableHttpClient build() {
        return HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    }

}
