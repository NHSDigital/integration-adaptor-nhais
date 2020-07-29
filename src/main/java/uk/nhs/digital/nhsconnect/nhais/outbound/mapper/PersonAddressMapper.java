package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

@Component
public class PersonAddressMapper implements FromFhirToEdifactMapper<PersonAddress> {

    public PersonAddress map(Parameters parameters) {
        Address address = getAddress(parameters);

        return PersonAddress.builder()
            .addressLine1(getAddressLineOrNull(address.getLine(), 0))
            .addressLine2(getAddressLineOrNull(address.getLine(), 1))
            .addressLine3(getAddressLineOrNull(address.getLine(), 2))
            .addressLine4(getAddressLineOrNull(address.getLine(), 3))
            .addressLine5(getAddressLineOrNull(address.getLine(), 4))
            .postalCode(address.getPostalCode())
            .build();
    }

    private Address getAddress(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);

        return patient.getAddress().stream()
            .findFirst()
            .orElseThrow(() -> new FhirValidationException("The Patient resource must contain an address"));
    }

    private String getAddressLineOrNull(List<StringType> addressLines, int index) {
        if (addressLines.size() <= index) {
            return null;
        }
        return addressLines.get(index).toString();
    }
}
