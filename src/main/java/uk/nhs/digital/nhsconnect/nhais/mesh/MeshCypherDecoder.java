package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MeshCypherDecoder {
    @Value("${nhais.mesh.cypherToMailbox}")
    private final String cypherToMailbox;

    @Autowired
    public MeshCypherDecoder(@Value("${nhais.mesh.cypherToMailbox}") String cypherToMailbox) {
        this.cypherToMailbox = cypherToMailbox;
    }

    public String getRecipientMailbox(OutboundMeshMessage outboundMeshMessage) {
        Map<String, String> mappings = createMappings();
        String recipient = outboundMeshMessage.getHaTradingPartnerCode();
        if (!mappings.containsKey(recipient)) {
            throw new MeshRecipientUnknownException("Couldn't decode recipient: " + recipient);
        }

        return mappings.get(recipient);
    }

    private Map<String, String> createMappings() {
        return Stream.of(cypherToMailbox.replaceAll(" ", "\n").split("\n"))
            .map(row -> row.split("="))
            .peek(this::validateMappings)
            .collect(Collectors.toMap(row -> row[0].strip(), row -> row[1].strip()));
    }

    private void validateMappings(String[] rows) {
        if (rows.length < 2) {
            throw new MeshRecipientUnknownException("NHAIS_MESH_CYPHER_TO_MAILBOX env var doesn't contain valid recipient to mailbox mapping");
        }
    }
}