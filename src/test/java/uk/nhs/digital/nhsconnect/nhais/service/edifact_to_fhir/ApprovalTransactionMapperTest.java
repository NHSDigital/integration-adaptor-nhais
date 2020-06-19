package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class ApprovalTransactionMapperTest {

    private static final String NHS_NUMBER = "1234567890";
    @Mock
    private Interchange interchange;
    @Mock
    private PersonName personName;

    @Test
    void testMap(SoftAssertions softly) {
        when(interchange.getPersonName()).thenReturn(Optional.of(personName));

        when(personName.getNhsNumber()).thenReturn(Optional.of(new NhsIdentifier(NHS_NUMBER)));

        var parameters = new Parameters()
            .addParameter(new PatientParameter(new Patient()));
        new ApprovalTransactionMapper().map(parameters, interchange);

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        softly.assertThat(parametersExt.size()).isEqualTo(1);
        Patient patient = parametersExt.extractPatient();
        softly.assertThat(patient.getIdentifierFirstRep().getValue()).isEqualTo(NHS_NUMBER);
        softly.assertThat(patient.getIdentifierFirstRep().getSystem()).isEqualTo(NhsIdentifier.SYSTEM);
    }

    @Test
    void testGetTransactionType() {
        assertThat(new ApprovalTransactionMapper().getTransactionType())
            .isEqualTo(ReferenceTransactionType.TransactionType.APPROVAL);
    }

}