package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;

@Component
public class EdifactToFhirService {

    public Parameters convertToFhir(Interchange interchange) {
        Patient patient = createPatient("PLACEHOLDER_FOR_PATIENT_ID"); //TODO: update placeholder with correct value

        Reference managingOrganization = createManagingOrganizationReference(interchange);
        Reference generalPractitioner = createGeneralPractitionerReference(interchange);

        patient.setManagingOrganization(managingOrganization);
        patient.addGeneralPractitioner(generalPractitioner);

        return createParameters(patient);
    }

    private Parameters createParameters(Resource... resources) {
        Parameters parameters = new Parameters();
        for (Resource resource : resources) {
            var parameterComponent = new Parameters.ParametersParameterComponent().setResource(resource);
            if(Patient.class.getSimpleName().equals(resource.fhirType())) {
                parameterComponent.setName(resource.fhirType().toLowerCase());
            }
            parameters.addParameter(parameterComponent);
        }
        return parameters;
    }

    private Reference createGeneralPractitionerReference(Interchange interchange) {
        String gpId = interchange.getGpNameAndAddress().getIdentifier();
        return new Reference(
                new Practitioner().setId(gpId)
            );
    }

    private Reference createManagingOrganizationReference(Interchange interchange) {
        String organizationId = interchange.getHealthAuthorityNameAndAddress().getIdentifier();
        return new Reference(
                new Organization().setId(organizationId)
            );
    }

    private Patient createPatient(String patientId) {
        return (Patient) new Patient().setId(patientId);
    }
}
