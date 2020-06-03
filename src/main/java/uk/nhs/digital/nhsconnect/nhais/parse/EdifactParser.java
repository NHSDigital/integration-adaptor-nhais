package uk.nhs.digital.nhsconnect.nhais.parse;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.EdifactMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;

public class EdifactParser {

    public Interchange parse(String edifact) {
        EdifactMessage edifactMessage = new EdifactMessage(edifact);
        return new Interchange(edifactMessage);
    }

}
