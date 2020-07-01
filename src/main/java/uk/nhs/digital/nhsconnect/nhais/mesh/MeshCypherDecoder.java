package uk.nhs.digital.nhsconnect.nhais.mesh;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;

@Component
public class MeshCypherDecoder {

    private final EdifactParser edifactParser;

    @Value("${nhais.mesh.cypherToMailbox}")
    private final String cypherToMailbox;

    @Autowired
    public MeshCypherDecoder(EdifactParser edifactParser,
                             @Value("${nhais.mesh.cypherToMailbox}") String cypherToMailbox) {
        this.edifactParser = edifactParser;
        this.cypherToMailbox = cypherToMailbox;
    }

    public String getRecipient(MeshMessage meshMessage) {
        Map<String, String> mappings = createMappings();

        String recipient = edifactParser.parse(meshMessage.getContent()).getInterchangeHeader().getRecipient();
        if(!mappings.containsKey(recipient)) {
            throw new MeshRecipientUnknownException("Couldn't decode recipient: " + recipient);
        }

        return mappings.get(recipient);
    }

    public String getSender(String edifactMessage) {
        Map<String, String> mappings = createMappings();

        String sender = edifactParser.parse(edifactMessage).getInterchangeHeader().getSender();
        if(!mappings.containsKey(sender)) {
            throw new MeshRecipientUnknownException("Couldn't decode sender: " + sender);
        }

        return mappings.get(sender);
    }

    private Map<String, String> createMappings() {
        return Stream.of(cypherToMailbox.replaceAll(" ", "\n").split("\n"))
                .map(row -> row.split("="))
                .collect(Collectors.toMap(row -> row[0].strip(), row -> row[1].strip()));
    }
}
