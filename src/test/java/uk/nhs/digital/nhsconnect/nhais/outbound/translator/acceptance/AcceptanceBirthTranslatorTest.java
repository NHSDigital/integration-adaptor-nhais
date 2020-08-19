package uk.nhs.digital.nhsconnect.nhais.outbound.translator.acceptance;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptanceBirthTranslatorTest {

    @Mock private OptionalInputValidator validator;

    @InjectMocks
    private AcceptanceBirthTranslator acceptanceBirthTranslator;

    @Test
    void When_MissingNhsNumberAndBirthPlace_Then_ThrowFhirValidationException() {
        Parameters parameters = new Parameters();
        when(validator.nhsNumberIsMissing(parameters)).thenReturn(true);

        assertThatThrownBy(() -> acceptanceBirthTranslator.translate(parameters))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage("NHS number of patient is missing");
    }

    @Test
    void when_MisssingSurname_Then_ThrowFhirValidationException() {
        Parameters parameters = new Parameters();
        when(validator.surnameIsMissing(parameters)).thenReturn(true);

        assertThatThrownBy(() -> acceptanceBirthTranslator.translate(parameters))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage("Surname of patient is missing");
    }

}