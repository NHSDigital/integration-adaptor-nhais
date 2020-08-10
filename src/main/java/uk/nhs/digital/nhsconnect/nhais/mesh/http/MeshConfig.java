package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.utils.PemFormatter;

@Component
@Getter
public class MeshConfig {

    private final String mailboxId;
    private final String mailboxPassword;
    private final String sharedKey;
    private final String host;
    private final String certValidation;
    private final String publicSuffix;
    private final String endpointCert;
    private final String endpointPrivateKey;
    private final String subCAcert;

    @Autowired
    public MeshConfig(
            @Value("${nhais.mesh.mailboxId}") String mailboxId,
            @Value("${nhais.mesh.mailboxPassword}") String mailboxPassword,
            @Value("${nhais.mesh.sharedKey}") String sharedKey,
            @Value("${nhais.mesh.host}") String host,
            @Value("${nhais.mesh.certValidation}") String certValidation,
            @Value("${nhais.mesh.publicSuffix}") String publicSuffix,
            @Value("${nhais.mesh.endpointCert}") String endpointCert,
            @Value("${nhais.mesh.endpointPrivateKey}") String endpointPrivateKey,
            @Value("${nhais.mesh.subCAcert}") String subCAcert) {
        this.mailboxId = mailboxId;
        this.mailboxPassword = mailboxPassword;
        this.sharedKey = sharedKey;
        this.host = host;
        this.certValidation = certValidation;
        this.publicSuffix = publicSuffix;
        this.endpointCert = endpointCert;
        this.endpointPrivateKey = endpointPrivateKey;
        this.subCAcert = subCAcert;
    }

    public String getEndpointCert() {
        return PemFormatter.format(endpointCert);
    }

    public String getEndpointPrivateKey() {
        return PemFormatter.format(endpointPrivateKey);
    }

    public String getSubCAcert() {
        return PemFormatter.format(subCAcert);
    }

}
