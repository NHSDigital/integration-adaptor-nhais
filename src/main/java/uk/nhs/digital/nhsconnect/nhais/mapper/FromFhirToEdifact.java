package uk.nhs.digital.nhsconnect.nhais.mapper;

import java.util.Arrays;
import java.util.List;

public class FromFhirToEdifact {

    private final List<FromFhirToEdifactMapper<?>> MAPPERS = Arrays.asList(
            new PersonNameMapper(),
            new AcceptanceTypeMapper()
    );

//    public Interchange map(Parameters parameters) {
//        List<?> collect = MAPPERS.stream()
//            .map(mapper -> mapper.map(parameters))
//            .collect(Collectors.toList());
//
//        return new Interchange().setSegments();
//    }
}
