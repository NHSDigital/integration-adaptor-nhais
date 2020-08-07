package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Getter
@Slf4j
public class MeshConfig {

    private final String mailboxId;
    private final String mailboxPassword;
    private final String sharedKey;
    private final String host;
    private final String endpointCert;
    private final String endpointPrivateKey;

    @Autowired
    public MeshConfig(
            @Value("${nhais.mesh.mailboxId}") String mailboxId,
            @Value("${nhais.mesh.mailboxPassword}") String mailboxPassword,
            @Value("${nhais.mesh.sharedKey}") String sharedKey,
            @Value("${nhais.mesh.host}") String host,
            @Value("${nhais.mesh.endpointCert}") String endpointCert,
            @Value("${nhais.mesh.endpointPrivateKey}") String endpointPrivateKey) {
        this.mailboxId = mailboxId;
        this.mailboxPassword = mailboxPassword;
        this.sharedKey = sharedKey;
        this.host = host;
        this.endpointCert = endpointCert;
        this.endpointPrivateKey = endpointPrivateKey;
    }

    public String getEndpointCert() {
        LOGGER.debug("RAW MESH ENDPOINT CERT: {}", endpointCert);
        String cert = trimExtraWhitespace(endpointCert);  //below computations are needed when default certificate is imported from application.yml
//        cert = cert.replaceAll("-----BEGIN CERTIFICATE-----", "");
//        cert = cert.replaceAll("-----END CERTIFICATE-----", "");
//        cert = cert.replaceAll(" ", "\n");
//        cert =  "-----BEGIN CERTIFICATE-----\n" + cert + "-----END CERTIFICATE-----";
        LOGGER.debug("TRANSFORMED MESH ENDPOINT CERT: {}", cert);
        return cert;
    }

    public String getEndpointPrivateKey() {
        LOGGER.debug("RAW MESH ENDPOINT PRIVATE KEY: {}", endpointPrivateKey);
        String key = trimExtraWhitespace(endpointPrivateKey);
//        key = key.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "");
//        key = key.replaceAll("-----END RSA PRIVATE KEY-----", "");
//        key = key.replaceAll(" ", "\n");
//        key = "-----BEGIN RSA PRIVATE KEY-----\n" + key + "-----END RSA PRIVATE KEY-----";
        LOGGER.debug("TRANSFORMED MESH ENDPOINT PRIVATE KEY: {}", key);
        return key;
    }

    private static final Pattern PEM_PATTERN = Pattern.compile("(-----[A-Z ]+-----)([^-]+)(-----[A-Z ]+-----)");

    private String trimExtraWhitespace(String value) {
        Matcher matcher = PEM_PATTERN.matcher(value);
        if(!matcher.matches()) {
            throw new RuntimeException("Invalid certificate or key format.");
        }
        String header = matcher.group(1).strip();
        String body = matcher.group(2);
        String footer = matcher.group(3).strip();

        body = Arrays.stream(body.split("\\s+"))
            .map(String::strip)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining("\n"));

        return String.join("\n", header, body, footer);
    }
}
