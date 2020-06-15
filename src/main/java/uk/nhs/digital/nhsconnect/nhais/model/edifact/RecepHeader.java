package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RecepHeader extends InterchangeHeader {

    public RecepHeader(@NonNull String sender, @NonNull String recipient, @NonNull Instant translationTime) {
        super(sender, recipient, translationTime);
    }

    public RecepHeader setSequenceNumber(Long sequenceNumber) {
        return (RecepHeader) super.setSequenceNumber(sequenceNumber);
    }

    @Override
    public String getValue() {
        return super.getValue() + "++RECEP+++EDIFACT TRANSFER";
    }
}
