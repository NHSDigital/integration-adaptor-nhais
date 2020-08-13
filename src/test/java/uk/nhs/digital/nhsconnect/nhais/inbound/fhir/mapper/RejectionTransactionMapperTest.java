package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class RejectionTransactionMapperTest {

    private static final String TEXT_LITERAL = "some_text_literal";
    @Mock
    private Transaction transaction;
    @Mock
    private FreeText freeText;
    @Mock
    private Message message;
    @Mock
    private Interchange interchange;
    @Mock
    private InterchangeHeader interchangeHeader;
    @Mock
    private HealthAuthorityNameAndAddress healthAuthorityNameAndAddress;
    @Mock
    private GpNameAndAddress gpNameAndAddress;

    @Test
    void testMap(SoftAssertions softly) {
        when(transaction.getFreeText()).thenReturn(Optional.of(freeText));
        when(transaction.getMessage()).thenReturn(message);
        when(transaction.getGpNameAndAddress()).thenReturn(gpNameAndAddress);
        when(message.getInterchange()).thenReturn(interchange);
        when(message.getHealthAuthorityNameAndAddress()).thenReturn(healthAuthorityNameAndAddress);
        when(interchange.getInterchangeHeader()).thenReturn(interchangeHeader);

        when(freeText.getFreeTextValue()).thenReturn(TEXT_LITERAL);

        var parameters = new RejectionTransactionMapper().map(transaction);

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        softly.assertThat(parameters.getParameter().size()).isEqualTo(3);
        softly.assertThat(parametersExt.extractValue(ParameterNames.FREE_TEXT)).isEqualTo(TEXT_LITERAL);
    }

    @Test
    void whenFreeTextIsMissing_expectException(SoftAssertions softly) {
        when(transaction.getFreeText()).thenReturn(Optional.empty());
        when(transaction.getMessage()).thenReturn(message);
        when(transaction.getGpNameAndAddress()).thenReturn(gpNameAndAddress);
        when(message.getInterchange()).thenReturn(interchange);
        when(message.getHealthAuthorityNameAndAddress()).thenReturn(healthAuthorityNameAndAddress);
        when(interchange.getInterchangeHeader()).thenReturn(interchangeHeader);

        softly.assertThatThrownBy(() -> new RejectionTransactionMapper().map(transaction))
            .isInstanceOf(EdifactValidationException.class);
    }

    @Test
    void testGetTransactionType() {
        assertThat(new RejectionTransactionMapper().getTransactionType())
            .isEqualTo(ReferenceTransactionType.Inbound.REJECTION);
    }
}
