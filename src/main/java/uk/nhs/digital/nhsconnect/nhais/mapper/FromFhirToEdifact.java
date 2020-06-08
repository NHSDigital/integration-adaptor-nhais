package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FromFhirToEdifact {
    private final List<FromFhirToEdifactMapper<?>> MAPPERS = Arrays.asList(
            new PersonNameMapper(),
            new AcceptanceTypeMapper(),
            new AcceptanceCodeMapper(),
            new PersonSexMapper(),
            new PersonAddressMapper(),
            new PersonAddressOldMapper(),
            new PersonDateOfBirthMapper(),
            new PersonGPMapper(),
            new PersonHAMapper(),
            new PersonDateOfEntryMapper(),
            new PersonGPPreviousMapper()
    );

    public List<Segment> map(Parameters parameters) {
        return MAPPERS.stream()
                .map(mapper -> mapper.map(parameters))
                .collect(Collectors.toList());
    }
}
