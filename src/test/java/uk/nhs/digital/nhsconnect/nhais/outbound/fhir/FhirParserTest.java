package uk.nhs.digital.nhsconnect.nhais.outbound.fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FhirParserTest {

    private FhirParser fhirParser = new FhirParser();

    @Test
    public void When_parseParameters_Then_returnParametersObject() throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(getClass().getResource("/patient/parameters.json").toURI())));
        Parameters parameters = fhirParser.parseParameters(json);
        assertThat(parameters.getParameter()).isEmpty();
    }

    @Test
    public void When_parseNotJson_Then_returnFhirValidationException(){
        String xml = "<item></item>";
        assertThatThrownBy(() -> fhirParser.parseParameters(xml)).isExactlyInstanceOf(FhirValidationException.class);
    }

}
