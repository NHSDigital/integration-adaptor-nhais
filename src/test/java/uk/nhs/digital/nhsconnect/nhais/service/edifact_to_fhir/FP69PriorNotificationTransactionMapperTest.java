package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FP69ExpiryDate;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FP69ReasonCode;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class FP69PriorNotificationTransactionMapperTest {

    private static final String NHS_NUMBER = "1234567890";
    private static final String SURNAME = "SMITH";
    private static final Instant DATE_OF_BIRTH = Instant.ofEpochSecond(123213);
    private static final int REASON_CODE = 123;
    private static final Instant EXPIRY_DATE = Instant.ofEpochSecond(4323423);
    private static final String FIRST_FORENAME = "JOHN";
    private static final String TITLE = "MR";
    private static final String ADDRESS_LINE_1 = null;
    private static final String ADDRESS_LINE_2 = "Main";
    private static final String ADDRESS_LINE_3 = "Other";
    private static final String ADDRESS_LINE_4 = "1/4";
    private static final String ADDRESS_LINE_5 = null;
    private static final String POSTAL_CODE = "ABC-123";
    @InjectMocks
    private FP69PriorNotificationTransactionMapper transactionMapper;

    private Parameters parameters;

    @Mock
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        parameters = new Parameters();
        parameters.addParameter(new PatientParameter().setName("patient").setResource(new Patient()));
    }

    @Test
    void whenPersonNameSegmentIsMissing_expectException() {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionMapper.map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("For an FP69 prior notification (reference F9) the PNA+PAT segment is required to provide the patient NHS number");
    }

    @Test
    void whenNhsNumberIsMissing_expectException() {
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder().build()));

        assertThatThrownBy(() -> transactionMapper.map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("For an FP69 prior notification (reference F9) the PNA+PAT segment is required to provide the patient NHS number");
    }

    @Test
    void whenSurnameIsMissing_expectException() {
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder()
            .nhsNumber(NHS_NUMBER)
            .build()));

        assertThatThrownBy(() -> transactionMapper.map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("For an FP69 prior notification (reference F9) the PNA+PAT segment is required to provide the patient surname");
    }

    @Test
    void whenDateOfBirthSegmentIsMissing_expectException() {
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder()
            .nhsNumber(NHS_NUMBER)
            .surname(SURNAME)
            .build()));
        when(transaction.getPersonDateOfBirth()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionMapper.map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("For an FP69 prior notification (reference F9) the DTM+329 segment is required to provide the patient date of birth");
    }

    @Test
    void whenReasonCodeSegmentIsMissing_expectException() {
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder()
            .nhsNumber(NHS_NUMBER)
            .surname(SURNAME)
            .build()));
        when(transaction.getPersonDateOfBirth()).thenReturn(Optional.of(PersonDateOfBirth.builder()
            .timestamp(DATE_OF_BIRTH)
            .build()));
        when(transaction.getFp69ReasonCode()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionMapper.map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("For an FP69 prior notification (reference F9) the HEA+FRN segment is required");
    }

    @Test
    void whenExpiryDateSegmentIsMissing_expectException() {
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder()
            .nhsNumber(NHS_NUMBER)
            .surname(SURNAME)
            .build()));
        when(transaction.getPersonDateOfBirth()).thenReturn(Optional.of(PersonDateOfBirth.builder()
            .timestamp(DATE_OF_BIRTH)
            .build()));
        when(transaction.getFp69ReasonCode()).thenReturn(Optional.of(FP69ReasonCode.builder()
            .code(REASON_CODE)
            .build()));
        when(transaction.getFp69ExpiryDate()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionMapper.map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("For an FP69 prior notification (reference F9) the DTM+962 segment is required");
    }

    @Test
    void whenMappingRequiredValues_expectParametersAreMapped(SoftAssertions softly) {
        mockRequiredEdifact();

        transactionMapper.map(parameters, transaction);

        assertRequiredFields(softly);
    }

    @Test
    void whenMappingAllValues_expectParametersAreMapped(SoftAssertions softly) {
        mockRequiredEdifact();
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder()
            .nhsNumber(NHS_NUMBER)
            .surname(SURNAME)
            .firstForename(FIRST_FORENAME)
            .title(TITLE)
            .build()));
        when(transaction.getPersonAddress()).thenReturn(Optional.of(PersonAddress.builder()
            .addressLine1(ADDRESS_LINE_1)
            .addressLine2(ADDRESS_LINE_2)
            .addressLine3(ADDRESS_LINE_3)
            .addressLine4(ADDRESS_LINE_4)
            .addressLine5(ADDRESS_LINE_5)
            .postalCode(POSTAL_CODE)
            .build()));

        transactionMapper.map(parameters, transaction);

        assertRequiredFields(softly);

        var patient = ParametersExtension.extractPatient(parameters);

        softly.assertThat(patient.getName()).hasSize(1);
        var givenNames = patient.getNameFirstRep().getGiven();
        softly.assertThat(givenNames).hasSize(1);
        softly.assertThat(givenNames.get(0).getValue()).isEqualTo(FIRST_FORENAME);

        softly.assertThat(patient.getAddress()).hasSize(1);
        var address = patient.getAddressFirstRep();
        softly.assertThat(address.getLine()).hasSize(5);
        softly.assertThat(address.getLine().get(0).getValue()).isEqualTo(ADDRESS_LINE_1);
        softly.assertThat(address.getLine().get(1).getValue()).isEqualTo(ADDRESS_LINE_2);
        softly.assertThat(address.getLine().get(2).getValue()).isEqualTo(ADDRESS_LINE_3);
        softly.assertThat(address.getLine().get(3).getValue()).isEqualTo(ADDRESS_LINE_4);
        softly.assertThat(address.getLine().get(4).getValue()).isEqualTo(ADDRESS_LINE_5);
        softly.assertThat(address.getPostalCode()).isEqualTo(POSTAL_CODE);
    }

    private void assertRequiredFields(SoftAssertions softly) {
        softly.assertThat(parameters.getParameter()).hasSize(3);

        var patient = ParametersExtension.extractPatient(parameters);
        softly.assertThat(patient.getName()).hasSize(1);
        softly.assertThat(patient.getNameFirstRep().getFamily()).isEqualTo(SURNAME);
        softly.assertThat(patient.getBirthDate()).isEqualToIgnoringHours("1970-01-02");
        softly.assertThat(patient.getIdentifier()).hasSize(1);
        softly.assertThat(patient.getIdentifierFirstRep().getSystem()).isEqualTo("https://fhir.nhs.uk/Id/nhs-number");
        softly.assertThat(patient.getIdentifierFirstRep().getValue()).isEqualTo(NHS_NUMBER);

        softly.assertThat(ParametersExtension.extractValue(parameters, "fp69ReasonCode"))
            .isEqualTo("123");
        softly.assertThat(ParametersExtension.extractValue(parameters, "fp69ExpiryDate"))
            .isEqualTo("1970-02-20");
    }

    private void mockRequiredEdifact() {
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder()
            .nhsNumber(NHS_NUMBER)
            .surname(SURNAME)
            .build()));
        when(transaction.getPersonDateOfBirth()).thenReturn(Optional.of(PersonDateOfBirth.builder()
            .timestamp(DATE_OF_BIRTH)
            .build()));
        when(transaction.getFp69ReasonCode()).thenReturn(Optional.of(FP69ReasonCode.builder()
            .code(REASON_CODE)
            .build()));
        when(transaction.getFp69ExpiryDate()).thenReturn(Optional.of(FP69ExpiryDate.builder()
            .timestamp(EXPIRY_DATE)
            .build()));
    }

    @Test
    void getTransactionType() {
        assertThat(transactionMapper.getTransactionType())
            .isEqualTo(ReferenceTransactionType.Inbound.FP69_PRIOR_NOTIFICATION);
    }
}
