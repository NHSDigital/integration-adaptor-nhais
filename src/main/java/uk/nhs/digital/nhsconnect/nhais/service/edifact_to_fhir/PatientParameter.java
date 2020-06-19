package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import ca.uhn.fhir.model.api.annotation.Block;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.GeneralPractitionerIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

@Block()
public class PatientParameter extends Parameters.ParametersParameterComponent {

    public PatientParameter(Interchange interchange) {
        Patient patient = new Patient();

        Reference managingOrganization = createManagingOrganizationReference(interchange);
        Reference generalPractitioner = createGeneralPractitionerReference(interchange);

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

    private Reference createManagingOrganizationReference(Interchange interchange) {
        String organizationId = interchange.getHealthAuthorityNameAndAddress().getIdentifier();
        return new Reference().setIdentifier(new ManagingOrganizationIdentifier(organizationId));
    }

    private Reference createGeneralPractitionerReference(Interchange interchange) {
        String gpId = interchange.getGpNameAndAddress().getIdentifier();
        return new Reference().setIdentifier(new GeneralPractitionerIdentifier(gpId));
    }
}
