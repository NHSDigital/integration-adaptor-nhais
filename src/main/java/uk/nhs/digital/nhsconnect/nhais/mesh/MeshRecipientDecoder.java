package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class MeshRecipientDecoder {

    private EdifactParser edifactParser;

    @Value("${nhais.mesh.recipientCodes}")
    private String recipientMapping;

    public String getRecipient(MeshMessage meshMessage) {
        Map<String, String> mappings = Stream.of(recipientMapping.replaceAll(" ", "\n").split("\n"))
            .map(row -> row.split("="))
            .collect(Collectors.toMap(row -> row[0].strip(), row -> row[1].strip()));

        String recipient = edifactParser.parse(meshMessage.getContent()).getInterchangeHeader().getRecipient();
        if(!mappings.containsKey(recipient)) {
            throw new MeshRecipientUnknownException("Couldn't decode recipient: " + recipient);
        }

        return mappings.get(recipient);
    }
}
