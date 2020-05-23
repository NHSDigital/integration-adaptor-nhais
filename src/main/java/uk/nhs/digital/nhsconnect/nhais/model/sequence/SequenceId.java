package uk.nhs.digital.nhsconnect.nhais.model.sequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "sequence")
public class SequenceId {
    @Id
    private String key;
    private Long sequenceNumber;
}
