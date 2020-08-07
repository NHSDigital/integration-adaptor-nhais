package uk.nhs.digital.nhsconnect.nhais.mesh.http;

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
    private final String endpointCert;
    private final String endpointPrivateKey;
    private final String subCAcert;
    private final String rootCA;

    @Autowired
    public MeshConfig(
            @Value("${nhais.mesh.mailboxId}") String mailboxId,
            @Value("${nhais.mesh.mailboxPassword}") String mailboxPassword,
            @Value("${nhais.mesh.sharedKey}") String sharedKey,
            @Value("${nhais.mesh.host}") String host,
            @Value("${nhais.mesh.endpointCert}") String endpointCert,
            @Value("${nhais.mesh.endpointPrivateKey}") String endpointPrivateKey,
            @Value("${nhais.mesh.subCAcert}") String subCAcert,
            @Value("${nhais.mesh.rootCA}") String rootCA) {
        this.mailboxId = mailboxId;
        this.mailboxPassword = mailboxPassword;
        this.sharedKey = sharedKey;
        this.host = host;
        this.endpointCert = endpointCert;
        this.endpointPrivateKey = endpointPrivateKey;
        this.subCAcert = subCAcert;
        this.rootCA = rootCA;
    }

    public String getEndpointCert() {
        String cert = endpointCert;  //below computations are needed when default certificate is imported from application.yml
        cert = cert.replaceAll("-----BEGIN CERTIFICATE-----", "");
        cert = cert.replaceAll("-----END CERTIFICATE-----", "");
        cert = cert.replaceAll(" ", "\n");
        return "-----BEGIN CERTIFICATE-----\n" + cert + "-----END CERTIFICATE-----";
    }

    public String getEndpointPrivateKey() {
        String key = endpointPrivateKey; //below computations are needed when default private key is imported from application.yml
        key = key.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "");
        key = key.replaceAll("-----END RSA PRIVATE KEY-----", "");
        key = key.replaceAll(" ", "\n");
        return "-----BEGIN RSA PRIVATE KEY-----\n" + key + "-----END RSA PRIVATE KEY-----";
    }

    public String getSubCAcert() {
        String cert = subCAcert; //below computations are needed when default private key is imported from application.yml
        cert = cert.replaceAll("-----BEGIN CERTIFICATE-----", "");
        cert = cert.replaceAll("-----END CERTIFICATE-----", "");
        cert = cert.replaceAll(" ", "\n");
        return "-----BEGIN CERTIFICATE-----\n" + cert + "-----END CERTIFICATE-----";
    }

    public String getRootCA() {
        String cert = rootCA; //below computations are needed when default private key is imported from application.yml
        cert = cert.replaceAll("-----BEGIN CERTIFICATE-----", "");
        cert = cert.replaceAll("-----END CERTIFICATE-----", "");
        cert = cert.replaceAll(" ", "\n");
        return "-----BEGIN CERTIFICATE-----\n" + cert + "-----END CERTIFICATE-----";
    }
}
