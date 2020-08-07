package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.Getter;
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
public class MeshConfig {

    private static final Pattern PEM_PATTERN = Pattern.compile("(-----[A-Z ]+-----)([^-]+)(-----[A-Z ]+-----)");

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
        return trimExtraWhitespace(endpointCert);
    }

    public String getEndpointPrivateKey() {
        return trimExtraWhitespace(endpointPrivateKey);
    }

    /**
     * Different methods of importing the certificates (application.yml, ENV, Cloud secret) can affect whitespace
     * and line delimiters. For these to be read as valid PEM files the whitespace needs to be stripped and newlines
     * included appropriately. This method parses and reformats these inputs into strings that can be read as PEM files.
     *
     * @param value the certificate or key to reform
     * @return the reformatted certificate or key
     */
    private String trimExtraWhitespace(String value) {
        Matcher matcher = PEM_PATTERN.matcher(value);

        if (!matcher.matches()) {
            throw new RuntimeException("Invalid certificate or key format");
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
