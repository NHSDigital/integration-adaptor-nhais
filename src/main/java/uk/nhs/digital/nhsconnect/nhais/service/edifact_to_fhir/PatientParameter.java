package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import ca.uhn.fhir.model.api.annotation.Block;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.GeneralPractitionerIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

@Block()
public class PatientParameter extends Parameters.ParametersParameterComponent {

    public PatientParameter(Transaction transaction) {
        Patient patient = new Patient();

        Reference managingOrganization = createManagingOrganizationReference(transaction);
        Reference generalPractitioner = createGeneralPractitionerReference(transaction);

        patient.setManagingOrganization(managingOrganization);
        patient.addGeneralPractitioner(generalPractitioner);

        this.setResource(patient);
        this.setName(ParameterNames.PATIENT);
    }

    public PatientParameter(Patient patient) {
        this.setResource(patient);
        this.setName(ParameterNames.PATIENT);
    }

    public PatientParameter() {
        this.setResource(new Patient());
        this.setName(ParameterNames.PATIENT);
    }

    private Reference createManagingOrganizationReference(Transaction transaction) {
        String organizationId = transaction.getMessage().getHealthAuthorityNameAndAddress().getIdentifier();
        return new Reference().setIdentifier(new ManagingOrganizationIdentifier(organizationId));
    }

    private Reference createGeneralPractitionerReference(Transaction transaction) {
        String gpId = transaction.getGpNameAndAddress().getIdentifier();
        return new Reference().setIdentifier(new GeneralPractitionerIdentifier(gpId));
    }
}
