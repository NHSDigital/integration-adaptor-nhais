package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.GeneralPractitionerIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.TransactionMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EdifactToFhirService {

    public final Map<ReferenceTransactionType.TransactionType, TransactionMapper> transactionMappers;

    public Parameters convertToFhir(Interchange interchange) {
        Patient patient = createPatient(interchange);

        var parameters = createParameters(patient);

        transactionMappers
            .get(interchange.getReferenceTransactionType().getTransactionType())
            .map(parameters, interchange);

        return parameters;
    }

    private Parameters createParameters(Resource... resources) {
        Parameters parameters = new Parameters();
        for (Resource resource : resources) {
            var parameterComponent = new Parameters.ParametersParameterComponent().setResource(resource);
            if (Patient.class.getSimpleName().equals(resource.fhirType())) {
                parameterComponent.setName(resource.fhirType().toLowerCase());
            }
            parameters.addParameter(parameterComponent);
        }
        return parameters;
    }

    private Reference createGeneralPractitionerReference(Interchange interchange) {
        String gpId = interchange.getGpNameAndAddress().getIdentifier();
        return new Reference().setIdentifier(new GeneralPractitionerIdentifier(gpId));
    }

    private Reference createManagingOrganizationReference(Interchange interchange) {
        String organizationId = interchange.getHealthAuthorityNameAndAddress().getIdentifier();
        return new Reference().setIdentifier(new ManagingOrganizationIdentifier(organizationId));
    }

    private Patient createPatient(Interchange interchange) {
        var patient = new Patient();

        Reference managingOrganization = createManagingOrganizationReference(interchange);
        Reference generalPractitioner = createGeneralPractitionerReference(interchange);

        patient.setManagingOrganization(managingOrganization);
        patient.addGeneralPractitioner(generalPractitioner);

        return patient;
    }
}
