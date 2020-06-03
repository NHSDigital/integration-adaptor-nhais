package uk.nhs.digital.nhsconnect.nhais.service;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class EdifactToFhirService {

    public Parameters convertToFhir(Interchange interchange) {
        Patient patient = createPatient("example");

        Reference managingOrganization = createManagingOrganizationReference(interchange);
        Reference generalPractitioner = createGeneralPractitionerReference(interchange);

        patient.setManagingOrganization(managingOrganization);
        patient.addGeneralPractitioner(generalPractitioner);

        return createParameters(patient);
    }

    private Parameters createParameters(Resource... resources) {
        Parameters parameters = new Parameters();
        for (Resource resource : resources) {
            parameters.addParameter(new Parameters.ParametersParameterComponent().setResource(resource));
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
