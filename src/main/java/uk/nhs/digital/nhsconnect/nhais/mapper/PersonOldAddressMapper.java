package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonOldAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

@Component
public class PersonOldAddressMapper implements OptionalFromFhirToEdifactMapper<PersonOldAddress> {

    public PersonOldAddress map(Parameters parameters) {
        Address address = getOldAddress(parameters);

        return PersonOldAddress.builder()
            .addressLine1(getAddressLineOrNull(address.getLine(), 0))
            .addressLine2(getAddressLineOrNull(address.getLine(), 1))
            .addressLine3(getAddressLineOrNull(address.getLine(), 2))
            .addressLine4(getAddressLineOrNull(address.getLine(), 3))
            .addressLine5(getAddressLineOrNull(address.getLine(), 4))
            .build();
    }

    private Address getOldAddress(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);

        return patient.getAddress().stream()
            .skip(1) // previous address is always the second occurrence of address in the Patient resource
            .findFirst()
            .orElseThrow(() -> new FhirValidationException("The second occurrence of address is required in the Patient resource"));
    }

    private String getAddressLineOrNull(List<StringType> addressLines, int index) {
        if (addressLines.size() <= index) {
            return null;
        }
        return addressLines.get(index).toString();
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        return ParametersExtension.extractPatient(parameters)
            .getAddress().size() > 1;
    }
}
