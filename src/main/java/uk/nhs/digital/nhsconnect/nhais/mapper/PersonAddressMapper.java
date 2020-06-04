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
        Patient patient = parameters.getParameter()
                .stream()
                .filter(param -> Patient.class.getSimpleName().equals(param.getName()))
                .map(Parameters.ParametersParameterComponent::getResource)
                .map(Patient.class::cast)
                .findFirst()
                .orElseThrow();

        return patient.getAddressFirstRep();
    }

    private String getAddressLineOrEmpty(List<StringType> addressLines, int index) {
        if (addressLines.size() <= index) {
            return null;
        }
        return addressLines.get(index).toString();
    }
}
