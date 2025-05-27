package uk.nhs.digital.nhsconnect.nhais.outbound.translator;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RegistrationMessageDateTime;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.acceptance.OptionalInputValidator;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.removal.RemovalTranslator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class RemovalTranslatorTest {

    private static final int BEGINNING_OF_MESSAGE_INDEX = 0;
    private static final int PARTY_QUALIFIER_INDEX = 1;
    private static final int DATETIME_INDEX = 2;
    private static final int INDEX = 3;
    private static final int FIRST_SEGMENT_GROUP_INDEX = 4;
    private static final int TRANSACTION_NUMBER = 5;
    private static final int GP_ADDRESS_INDEX = 6;
    private static final int FREE_TEXT_INDEX = 7;
    private static final int SECOND_SENGMENT_GROUP_INDEX = 8;
    private static final int PERSON_NAME_INDEX = 9;
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
    void When_NhsNumberIsMissing_Expect_ExceptionIsThrown() {
        when(validator.nhsNumberIsMissing(any())).thenReturn(true);

        assertThatThrownBy(() -> removalTranslator.translate(parameters))
            .isInstanceOf(FhirValidationException.class)
            .hasMessage("Patient resource property /identifier/0/value (NHS Number) is required");
    }

    @Test
    void When_FhirRemovalIsTranslated_Expect_AllRequiredSegmentsArePresentAndAreOfCorrectType(SoftAssertions softly) {
        final int expectedSegmentCount = 10;

        when(validator.nhsNumberIsMissing(any())).thenReturn(false);
        when(partyQualifierMapper.map(parameters)).thenReturn(partyQualifier);
        when(gpNameAndAddressMapper.map(parameters)).thenReturn(gpNameAndAddress);
        when(personNameMapper.map(parameters)).thenReturn(personName);
        when(freeTextMapper.map(parameters)).thenReturn(freeText);

        List<Segment> segments = removalTranslator.translate(parameters);

        softly.assertThat(segments.size()).isEqualTo(expectedSegmentCount);

        softly.assertThat(segments.get(BEGINNING_OF_MESSAGE_INDEX)).isExactlyInstanceOf(BeginningOfMessage.class);
        softly.assertThat(segments.get(PARTY_QUALIFIER_INDEX)).isEqualTo(partyQualifier);
        softly.assertThat(segments.get(DATETIME_INDEX)).isExactlyInstanceOf(RegistrationMessageDateTime.class);
        softly.assertThat(segments.get(INDEX)).isExactlyInstanceOf(ReferenceTransactionType.class);
        softly.assertThat(segments.get(FIRST_SEGMENT_GROUP_INDEX)).isExactlyInstanceOf(SegmentGroup.class);
        softly.assertThat(segments.get(TRANSACTION_NUMBER)).isExactlyInstanceOf(ReferenceTransactionNumber.class);
        softly.assertThat(segments.get(GP_ADDRESS_INDEX)).isEqualTo(gpNameAndAddress);
        softly.assertThat(segments.get(FREE_TEXT_INDEX)).isEqualTo(freeText);
        softly.assertThat(segments.get(SECOND_SENGMENT_GROUP_INDEX)).isExactlyInstanceOf(SegmentGroup.class);
        softly.assertThat(segments.get(PERSON_NAME_INDEX)).isEqualTo(personName);
    }
}
