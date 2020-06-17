package uk.nhs.digital.nhsconnect.nhais.parse;

import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;

import java.util.List;
import java.util.stream.Collectors;

public class EdifactParser {
    public Interchange parse(String edifact) {
        EdifactMessage edifactMessage = new EdifactMessage(edifact);
        Interchange interchange = new Interchange(edifactMessage);
        validateInterchange(interchange);

        return interchange;
    }

    private void validateInterchange(Interchange interchange) {
        List<ToEdifactParsingException> exceptions = interchange.validate();
        String errorList = exceptions.stream()
            .map(Exception::getMessage)
            .collect(Collectors.joining(", "));

        if (StringUtils.isNotEmpty(errorList)) {
            throw new ToEdifactParsingException(errorList);
        }
    }
}
