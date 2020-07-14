package uk.nhs.digital.nhsconnect.nhais.translator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonNameMapper;
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
import uk.nhs.digital.nhsconnect.nhais.translator.removal.RemovalTranslator;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void whenFhirRemovalIsTranslated_thenAllRequiredSegmentsArePresentAndAreOfCorrectType() {
        when(partyQualifierMapper.map(parameters)).thenReturn(partyQualifier);
        when(gpNameAndAddressMapper.map(parameters)).thenReturn(gpNameAndAddress);
        when(personNameMapper.map(parameters)).thenReturn(personName);
        when(freeTextMapper.map(parameters)).thenReturn(freeText);

        List<Segment> segments = removalTranslator.translate(parameters);

        assertThat(segments.size()).isEqualTo(10);

        assertThat(segments.get(0)).isExactlyInstanceOf(BeginningOfMessage.class);
        assertThat(segments.get(1)).isEqualTo(partyQualifier);
        assertThat(segments.get(2)).isExactlyInstanceOf(DateTimePeriod.class);
        assertThat(segments.get(3)).isExactlyInstanceOf(ReferenceTransactionType.class);
        assertThat(segments.get(4)).isExactlyInstanceOf(SegmentGroup.class);
        assertThat(segments.get(5)).isExactlyInstanceOf(ReferenceTransactionNumber.class);
        assertThat(segments.get(6)).isEqualTo(gpNameAndAddress);
        assertThat(segments.get(7)).isEqualTo(freeText);
        assertThat(segments.get(8)).isExactlyInstanceOf(SegmentGroup.class);
        assertThat(segments.get(9)).isEqualTo(personName);
    }
}
