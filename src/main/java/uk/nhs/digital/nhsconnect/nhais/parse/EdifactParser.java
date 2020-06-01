package uk.nhs.digital.nhsconnect.nhais.parse;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;

import java.util.Collections;

public class EdifactParser {

    public Interchange parse(String edifact) {
        return new Interchange(Collections.emptyList());
    }

}
