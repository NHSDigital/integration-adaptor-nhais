package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class RejectionTransactionMapperTest {

    private static final String TEXT_LITERAL = "some_text_literal";
    @Mock
    Interchange interchange;
    @Mock
    FreeText freeText;

    @Test
    void testMap(SoftAssertions softly) {
        when(interchange.getFreeText()).thenReturn(freeText);

        when(freeText.getTextLiteral()).thenReturn(TEXT_LITERAL);

        var parameters = new Parameters();
        new RejectionTransactionMapper().map(parameters, interchange);

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        softly.assertThat(parameters.getParameter().size()).isEqualTo(1);
        softly.assertThat(parametersExt.extractValue(ParameterNames.FREE_TEXT)).isEqualTo(TEXT_LITERAL);
    }

    @Test
    void testGetTransactionType() {
        assertThat(new RejectionTransactionMapper().getTransactionType())
            .isEqualTo(ReferenceTransactionType.TransactionType.REJECTION);
    }
}
