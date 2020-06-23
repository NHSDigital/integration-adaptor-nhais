package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MeshConfig {

    private final String mailboxId;
    private final String mailboxPassword;
    private final String sharedKey;
    private final String host;
    private final String port;
    private final String endpointCert;
    private final String endpointPrivateKey;
    private final String caCert;

    @Autowired
    public MeshConfig(
            @Value("${nhais.mesh.mailboxId}") String mailboxId,
            @Value("${nhais.mesh.mailboxPassword}") String mailboxPassword,
            @Value("${nhais.mesh.sharedKey}") String sharedKey,
            @Value("${nhais.mesh.host}") String host,
            @Value("${nhais.mesh.port}") String port,
            @Value("${nhais.mesh.endpointCert}") String endpointCert,
            @Value("${nhais.mesh.endpointPrivateKey}") String endpointPrivateKey,
            @Value("${nhais.mesh.caCert}") String caCert) {
        this.mailboxId = mailboxId;
        this.mailboxPassword = mailboxPassword;
        this.sharedKey = sharedKey;
        this.host = host;
        this.port = port;
        this.endpointCert = endpointCert;
        this.endpointPrivateKey = endpointPrivateKey;
        this.caCert = caCert;
    }
}
