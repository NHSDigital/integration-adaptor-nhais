package uk.nhs.digital.nhsconnect.nhais.outbound;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import java.util.List;

public class FhirElementsUtils {

    public static void checkGpCodePresence(Patient patient) {
        String path = "patient.generalPractitioner";
        exceptionIfMissingOrEmpty(path, patient.getGeneralPractitioner());
        path += ".identifier";
        exceptionIfMissingOrEmpty(path, patient.getGeneralPractitionerFirstRep().getIdentifier());
        path += ".value";
        exceptionIfMissingOrEmpty(path, patient.getGeneralPractitionerFirstRep().getIdentifier().getValue());
    }

    public static void checkHaCipherPresence(Patient patient) {
        Reference haReference = patient.getManagingOrganization();
        String path = "patient.managingOrganization";
        exceptionIfMissingOrEmpty(path, patient.getManagingOrganization());
        exceptionIfMissingOrEmpty(path, haReference);
        path += ".identifier";
        exceptionIfMissingOrEmpty(path, haReference.getIdentifier());
        Identifier gpId = haReference.getIdentifier();
        exceptionIfMissingOrEmpty(path, gpId);
        path += ".value";
        exceptionIfMissingOrEmpty(path, gpId.getValue());
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
