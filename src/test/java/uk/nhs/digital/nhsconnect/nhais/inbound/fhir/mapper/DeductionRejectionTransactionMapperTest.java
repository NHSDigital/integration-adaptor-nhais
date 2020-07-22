package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.PatientParameter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class DeductionRejectionTransactionMapperTest {

    private static final String NHS_NUMBER = "1234567890";

    @Mock
    private Transaction transaction;

    @Mock
    private PersonName personName;

    @Test
    void when_AllDataPresent_then_mapped(SoftAssertions softly) {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getNhsNumber()).thenReturn(NHS_NUMBER);
        when(transaction.getFreeText()).thenReturn(Optional.of(new FreeText("TEXT VALUE")));

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        new DeductionRejectionTransactionMapper().map(parameters, transaction);

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        softly.assertThat(parametersExt.size()).isEqualTo(2);
        Patient patient = parametersExt.extractPatient();
        softly.assertThat(patient.getIdentifierFirstRep().getValue()).isEqualTo(NHS_NUMBER);
        softly.assertThat(patient.getIdentifierFirstRep().getSystem()).isEqualTo(NhsIdentifier.SYSTEM);
        softly.assertThat(parametersExt.extractValue(ParameterNames.FREE_TEXT)).isEqualTo("TEXT VALUE");
    }

    @Test
    void when_missingOfficialPatientIdentifier_then_throwsException() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getNhsNumber()).thenReturn(null);

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        assertThatThrownBy(() -> new DeductionRejectionTransactionMapper().map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessageContaining("NHS Number");
    }

    @Test
    void when_missingPersonNameSegment_then_throwsException() {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        assertThatThrownBy(() -> new DeductionRejectionTransactionMapper().map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessageContaining("NHS Number");
    }

    @Test
    void when_missingHaNotes_then_throwsException() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getNhsNumber()).thenReturn(NHS_NUMBER);
        when(transaction.getFreeText()).thenReturn(Optional.empty());

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        assertThatThrownBy(() -> new DeductionRejectionTransactionMapper().map(parameters, transaction))
            .isInstanceOf(EdifactValidationException.class)
            .hasMessageContaining("HA Notes");
    }

    @Test
    void testGetTransactionType() {
        assertThat(new DeductionRejectionTransactionMapper().getTransactionType())
            .isEqualTo(ReferenceTransactionType.Inbound.DEDUCTION_REJECTION);
    }

}