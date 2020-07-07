package uk.nhs.digital.nhsconnect.nhais.utils;

import java.util.List;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

public class FhirElementUtils {

    public static void checkGpCodePresence(Patient patient) {
        String path = "patient.generalPractitioner";
        FhirElementUtils.exceptionIfMissingOrEmpty(path, patient.getGeneralPractitioner());
        path += ".identifier";
        FhirElementUtils.exceptionIfMissingOrEmpty(path, patient.getGeneralPractitionerFirstRep().getIdentifier());
        path += ".value";
        FhirElementUtils.exceptionIfMissingOrEmpty(path, patient.getGeneralPractitionerFirstRep().getIdentifier().getValue());
    }

    public static void checkManagingOrganizationPresence(Patient patient) {
        String path = "patient.managingOrganization";
        FhirElementUtils.exceptionIfMissingOrEmpty(path, patient.getManagingOrganization());
        path += ".identifier";
        FhirElementUtils.exceptionIfMissingOrEmpty(path, patient.getManagingOrganization().getIdentifier());
        path += ".value";
        FhirElementUtils.exceptionIfMissingOrEmpty(path, patient.getManagingOrganization().getIdentifier().getValue());
    }

    private static void exceptionIfMissingOrEmpty(String path, Object value) throws FhirValidationException {
        if (value == null) {
            throw new FhirValidationException("Missing element at " + path);
        }
        if (value instanceof List) {
            List list = (List) value;
            if (list.isEmpty()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        } else if (value instanceof String) {
            String str = (String) value;
            if (str.isBlank()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        }
    }
}
