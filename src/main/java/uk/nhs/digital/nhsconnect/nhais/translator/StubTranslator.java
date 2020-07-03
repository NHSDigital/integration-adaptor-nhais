package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.util.Arrays;
import java.util.List;

/**
 * @deprecated for removal after completion of last translate outbound transaction story. Useful to stub out APIs until
 * then
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Deprecated
public class StubTranslator implements FhirToEdifactTranslator {

    private final static String GP_CODE = "900";

    private final FhirParser fhirParser;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        throw new FhirValidationException("Please use translate(isA(Parameters.class),isA(TransactionType.class)");
    }

    public List<Segment> translate(Parameters parameters, ReferenceTransactionType.TransactionType transactionType) throws FhirValidationException {
        return Arrays.asList(
            new BeginningOfMessage(),
            new NameAndAddress(getHaCipher(parameters), NameAndAddress.QualifierAndCode.FHS),
            new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP),
            new ReferenceTransactionType(transactionType),
            new SegmentGroup(1),
            new ReferenceTransactionNumber(),
            getGp(parameters)
        );
    }

    private GpNameAndAddress getGp(Parameters parameters) {
        return GpNameAndAddress.builder()
            .identifier(getPersonGP(parameters))
            .code(GP_CODE)
            .build();
    }

    private String getPersonGP(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getGeneralPractitionerFirstRep().getIdentifier().getValue();
    }

    private String getHaCipher(Parameters parameters) throws FhirValidationException {
        Patient patient = ParametersExtension.extractPatient(parameters);
        String path = "patient.managingOrganization";
        exceptionIfMissingOrEmpty(path, patient.getManagingOrganization());
        Reference haReference = patient.getManagingOrganization();
        return getOrganizationIdentifier(path, haReference);
    }

    @Deprecated
    private String getOrganizationIdentifier(String path, Reference reference) throws FhirValidationException {
        exceptionIfMissingOrEmpty(path, reference);
        path += ".identifier";
        exceptionIfMissingOrEmpty(path, reference.getIdentifier());
        Identifier gpId = reference.getIdentifier();
        exceptionIfMissingOrEmpty(path, gpId);
        path += ".value";
        exceptionIfMissingOrEmpty(path, gpId.getValue());
        return gpId.getValue();
    }

    @Deprecated
    private void exceptionIfMissingOrEmpty(String path, Object value) throws FhirValidationException {
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
