package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonOldAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

public class PersonOldAddressMapper implements FromFhirToEdifactMapper<PersonOldAddress> {
    private final static Address.AddressUse ADDRESS_USE_OLD = Address.AddressUse.OLD;

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
            .filter(address -> address.getUse().equals(ADDRESS_USE_OLD))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Previous Address mapping problem"));
    }

    private String getAddressLineOrNull(List<StringType> addressLines, int index) {
        if (addressLines.size() <= index) {
            return null;
        }
        return addressLines.get(index).toString();
    }
}
