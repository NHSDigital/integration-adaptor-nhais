package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class RejectionTransactionMapperTest {

    private static final String RECIPIENT = "some_recipient";
    private static final String TEXT_LITERAL = "some_text_literal";
    @Mock
    Interchange interchange;
    @Mock
    FreeText freeText;
    @Mock
    InterchangeHeader interchangeHeader;

    @Test
    void testMap(SoftAssertions softly) {
        when(interchange.getInterchangeHeader()).thenReturn(interchangeHeader);
        when(interchange.getFreeText()).thenReturn(freeText);

        when(interchangeHeader.getRecipient()).thenReturn(RECIPIENT);
        when(freeText.getTextLiteral()).thenReturn(TEXT_LITERAL);

        var parameters = new Parameters();
        new RejectionTransactionMapper().map(parameters, interchange);

        var parameterComponents = parameters.getParameter();
        softly.assertThat(parameterComponents.size()).isEqualTo(2);
        softly.assertThat(parameterComponents.get(0).getName()).isEqualTo("freeText");
        softly.assertThat(((StringType) parameterComponents.get(0).getValue()).getValue()).isEqualTo(TEXT_LITERAL);

        softly.assertThat(parameterComponents.get(1).getName()).isEqualTo("gpTradingPartnerCode");
        softly.assertThat(((StringType) parameterComponents.get(1).getValue()).getValue()).isEqualTo(RECIPIENT);
    }

    @Test
    void testGetTransactionType() {
        assertThat(new RejectionTransactionMapper().getTransactionType())
            .isEqualTo(ReferenceTransactionType.TransactionType.REJECTION);
    }
}
