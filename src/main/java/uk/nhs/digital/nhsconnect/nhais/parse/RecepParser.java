package uk.nhs.digital.nhsconnect.nhais.parse;

import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepMessage;

@Component
public class RecepParser {
    public Recep parse(String recep) {
        var recepMessage = new RecepMessage(recep);
        return new Recep(recepMessage);
    }
}
