package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;

import java.util.List;

public class PersonAddressOldMapper implements FromFhirToEdifactMapper<PersonAddress> {
    private final static Address.AddressUse ADDRESS_USE_HOME = Address.AddressUse.OLD;

    public PersonAddress map(Parameters parameters) {
        Address address = getAddress(parameters);

        //Not sure if OLD address needs any validation re-using PersonAddress for now
        return PersonAddress.builder()
                .addressText(address.getText())
                .addressLine1(getAddressLineOrNull(address.getLine(), 0))
                .addressLine2(getAddressLineOrNull(address.getLine(), 1))
                .build();
    }

    private Address getAddress(Parameters parameters) {
        Patient patient = getPatient(parameters);

        return patient.getAddress().stream()
                .filter(address -> address.getUse().equals(ADDRESS_USE_HOME))
                .findFirst()
                .orElseThrow();
    }

    private String getAddressLineOrNull(List<StringType> addressLines, int index) {
        if (addressLines.size() <= index) {
            return null;
        }
        return addressLines.get(index).toString();
    }
}
