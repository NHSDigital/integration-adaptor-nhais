package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TranslatedInterchange {

    private String edifact;
    private InterchangeType interchangeType;
    private String operationId;
    // other metadata needed for processing

    public enum InterchangeType {
        REGISTRATION, RECEP;
    }
}
