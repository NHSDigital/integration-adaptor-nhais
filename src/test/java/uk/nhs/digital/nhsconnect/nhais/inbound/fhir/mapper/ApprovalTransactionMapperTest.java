package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class ApprovalTransactionMapperTest {

    private static final String NHS_NUMBER = "1234567890";
    @Mock
    private Transaction transaction;
    @Mock
    private PersonName personName;

    @Test
    void testMap(SoftAssertions softly) {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getNhsNumber()).thenReturn(NHS_NUMBER);

        var parameters = new ApprovalTransactionMapper().map(transaction);

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        softly.assertThat(parametersExt.size()).isEqualTo(1);
        Patient patient = parametersExt.extractPatient();
        softly.assertThat(patient.getIdentifierFirstRep().getValue()).isEqualTo(NHS_NUMBER);
        softly.assertThat(patient.getIdentifierFirstRep().getSystem()).isEqualTo(NhsIdentifier.SYSTEM);
    }

    @Test
    void testGetTransactionType() {
        assertThat(new ApprovalTransactionMapper().getTransactionType())
            .isEqualTo(ReferenceTransactionType.Inbound.APPROVAL);
    }

}