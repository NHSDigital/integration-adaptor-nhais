package uk.nhs.digital.nhsconnect.nhais.mesh;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class MeshRecipientDecoder {

    @Value("${nhais.mesh.recipientCodes}")
    private String recipientMapping;

    public String getRecipient(MeshMessage meshMessage) {
        Map<String, String> mappings = Stream.of(recipientMapping.replaceAll(" ", "\n").split("\n"))
            .map(row -> row.split("="))
            .collect(Collectors.toMap(row -> row[0].strip(), row -> row[1].strip()));

        String recipient = new EdifactMessage(meshMessage.getContent()).getInterchangeHeader().getRecipient();
        if(!mappings.containsKey(recipient)) {
            throw new MeshRecipientUnknownException("Couldn't decode recipient: " + recipient);
        }

        return mappings.get(recipient);
    }
}
