package uk.nhs.digital.nhsconnect.nhais.service;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.GeneralPractitionerIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.TransactionMapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EdifactToFhirService {

    public final Map<ReferenceTransactionType.TransactionType, TransactionMapper> transactionMappers;

    public Parameters convertToFhir(Interchange interchange) {
        var patient = createPatient(interchange);
        var gpTradingPartnerCode = createGpTradingPartnerCode(interchange);

        var parameters = new Parameters()
            .addParameter(gpTradingPartnerCode)
            .addParameter(patient);

        transactionMappers
            .get(interchange.getReferenceTransactionType().getTransactionType())
            .map(parameters, interchange);

        return parameters;
    }

    private Parameters.ParametersParameterComponent createGpTradingPartnerCode(Interchange interchange) {
        String recipient = interchange.getInterchangeHeader().getRecipient();
        return new Parameters.ParametersParameterComponent()
            .setName("gpTradingPartnerCode")
            .setValue(new StringType(recipient));
    }

    private Parameters.ParametersParameterComponent createPatient(Interchange interchange) {
        Patient patient = new Patient();

        interchange.getPatientIdentifier()
            .flatMap(PatientIdentifier::getNhsNumber)
            .ifPresent(nhsIdentifier -> patient.setIdentifier(List.of(nhsIdentifier)));

        return new Parameters.ParametersParameterComponent()
            .setResource(patient)
            .setName(patient.fhirType().toLowerCase());
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
