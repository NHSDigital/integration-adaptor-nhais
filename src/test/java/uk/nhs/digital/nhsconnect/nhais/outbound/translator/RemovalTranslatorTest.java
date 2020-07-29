package uk.nhs.digital.nhsconnect.nhais.outbound.translator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.acceptance.OptionalInputValidator;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.removal.RemovalTranslator;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class RemovalTranslatorTest {

    @Mock
    private PartyQualifierMapper partyQualifierMapper;

    @Mock
    private GpNameAndAddressMapper gpNameAndAddressMapper;

    @Mock
    private PersonNameMapper personNameMapper;

    @Mock
    private FreeTextMapper freeTextMapper;

    @Mock
    private Parameters parameters;

    @InjectMocks
    private RemovalTranslator removalTranslator;

    @Mock
    private PartyQualifier partyQualifier;

    @Mock
    private GpNameAndAddress gpNameAndAddress;

    @Mock
    private PersonName personName;

    @Mock
    private FreeText freeText;

    @Mock
    private OptionalInputValidator validator;

    @Test
    void whenNhsNumberIsMissing_thenExceptionIsThrown() {
        when(validator.nhsNumberIsMissing(any())).thenReturn(true);

        assertThatThrownBy(() -> removalTranslator.translate(parameters))
            .isInstanceOf(FhirValidationException.class)
            .hasMessage("Patient resource property /identifier/0/value (NHS Number) is required");
    }

    @Test
    void whenFhirRemovalIsTranslated_thenAllRequiredSegmentsArePresentAndAreOfCorrectType(SoftAssertions softly) {
        when(validator.nhsNumberIsMissing(any())).thenReturn(false);
        when(partyQualifierMapper.map(parameters)).thenReturn(partyQualifier);
        when(gpNameAndAddressMapper.map(parameters)).thenReturn(gpNameAndAddress);
        when(personNameMapper.map(parameters)).thenReturn(personName);
        when(freeTextMapper.map(parameters)).thenReturn(freeText);

        List<Segment> segments = removalTranslator.translate(parameters);

        softly.assertThat(segments.size()).isEqualTo(10);

        softly.assertThat(segments.get(0)).isExactlyInstanceOf(BeginningOfMessage.class);
        softly.assertThat(segments.get(1)).isEqualTo(partyQualifier);
        softly.assertThat(segments.get(2)).isExactlyInstanceOf(DateTimePeriod.class);
        softly.assertThat(segments.get(3)).isExactlyInstanceOf(ReferenceTransactionType.class);
        softly.assertThat(segments.get(4)).isExactlyInstanceOf(SegmentGroup.class);
        softly.assertThat(segments.get(5)).isExactlyInstanceOf(ReferenceTransactionNumber.class);
        softly.assertThat(segments.get(6)).isEqualTo(gpNameAndAddress);
        softly.assertThat(segments.get(7)).isEqualTo(freeText);
        softly.assertThat(segments.get(8)).isExactlyInstanceOf(SegmentGroup.class);
        softly.assertThat(segments.get(9)).isEqualTo(personName);
    }
}
