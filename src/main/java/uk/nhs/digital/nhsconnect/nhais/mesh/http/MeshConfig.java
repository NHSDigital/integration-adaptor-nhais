package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.nhs.digital.nhsconnect.nhais.utils.PemFormatter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "nhais.mesh")
public class MeshConfig {
    private String mailboxId;
    private String mailboxPassword;
    private String sharedKey;
    private String host;
    private String certValidation;
    private String endpointCert;
    private String endpointPrivateKey;
    private String subCAcert;

    public String getFormattedEndpointCert() {
        return PemFormatter.format(endpointCert);
    }

    public String getFormattedEndpointPrivateKey() {
        return PemFormatter.format(endpointPrivateKey);
    }

    public String getFormattedSubCaCert() {
        return PemFormatter.format(subCAcert);
    }
}