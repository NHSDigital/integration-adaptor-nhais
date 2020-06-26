package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class RejectionTransactionMapperTest {

    private static final String TEXT_LITERAL = "some_text_literal";
    @Mock
    TransactionV2 transaction;
    @Mock
    FreeText freeText;

    @Test
    void testMap(SoftAssertions softly) {
        when(transaction.getFreeText()).thenReturn(freeText);

        when(freeText.getTextLiteral()).thenReturn(TEXT_LITERAL);

        var parameters = new Parameters();
        new RejectionTransactionMapper().map(parameters, transaction);

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
