package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;

import java.util.List;

public class PersonAddressMapper implements FromFhirToEdifactMapper<PersonAddress> {

    public PersonAddress map(Parameters parameters) {
        Address address = getAddress(parameters);

        return PersonAddress.builder()
                .addressText(address.getText())
                .addressLine1(getAddressLineOrEmpty(address.getLine(), 0))
                .addressLine2(getAddressLineOrEmpty(address.getLine(), 1))
                .build();
    }

    private Address getAddress(Parameters parameters) {
        Patient patient = getPatient(parameters);

        return patient.getAddress().stream()
                .filter(address -> address.getUse().equals(Address.AddressUse.HOME))
                .findFirst()
                .orElseThrow();
    }

    private String getAddressLineOrEmpty(List<StringType> addressLines, int index) {
        if (addressLines.size() <= index) {
            return null;
        }
        return addressLines.get(index).toString();
    }
}
