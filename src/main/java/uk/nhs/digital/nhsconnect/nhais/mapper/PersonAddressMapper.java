package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

public class PersonAddressMapper implements FromFhirToEdifactMapper<PersonAddress> {
    private final static Address.AddressUse ADDRESS_USE_HOME = Address.AddressUse.HOME;

    public PersonAddress map(Parameters parameters) {
        Address address = getAddress(parameters);

        return PersonAddress.builder()
            .addressLine1(getAddressLineOrNull(address.getLine(), 0))
            .addressLine2(getAddressLineOrNull(address.getLine(), 1))
            .addressLine3(getAddressLineOrNull(address.getLine(), 2))
            .addressLine4(getAddressLineOrNull(address.getLine(), 3))
            .addressLine5(getAddressLineOrNull(address.getLine(), 4))
            .build();
    }

    private Address getAddress(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);

        return patient.getAddress().stream()
            .filter(address -> address.getUse().equals(ADDRESS_USE_HOME))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Address mapping problem"));
    }

    private String getAddressLineOrNull(List<StringType> addressLines, int index) {
        if (addressLines.size() <= index) {
            return null;
        }
        return addressLines.get(index).toString();
    }
}
