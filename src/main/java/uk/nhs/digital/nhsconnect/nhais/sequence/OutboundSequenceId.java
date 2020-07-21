package uk.nhs.digital.nhsconnect.nhais.sequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class OutboundSequenceId {
    @Id
    private String key;
    private Long sequenceNumber;
}
