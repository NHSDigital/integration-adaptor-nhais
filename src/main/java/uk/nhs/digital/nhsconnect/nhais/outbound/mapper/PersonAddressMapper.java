package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.utils.EdifactAddressPart;

import java.util.List;

@Component
public class PersonAddressMapper implements FromFhirToEdifactMapper<PersonAddress> {
    public PersonAddress map(Parameters parameters) {
        Address address = getAddress(parameters);

        return PersonAddress.builder()
            .addressLine1(getAddressLineOrNull(address.getLine(), EdifactAddressPart.ADDRESS_LINE_1_INDEX))
            .addressLine2(getAddressLineOrNull(address.getLine(), EdifactAddressPart.ADDRESS_LINE_2_INDEX))
            .addressLine3(getAddressLineOrNull(address.getLine(), EdifactAddressPart.ADDRESS_LINE_3_INDEX))
            .addressLine4(getAddressLineOrNull(address.getLine(), EdifactAddressPart.ADDRESS_LINE_4_INDEX))
            .addressLine5(getAddressLineOrNull(address.getLine(), EdifactAddressPart.ADDRESS_LINE_5_INDEX))
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
