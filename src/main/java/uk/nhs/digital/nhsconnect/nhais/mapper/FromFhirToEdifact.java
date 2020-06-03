package uk.nhs.digital.nhsconnect.nhais.mapper;

import java.util.Arrays;
import java.util.List;

public class FromFhirToEdifact {

    //TODO work in progress
    private final List<FromFhirToEdifactMapper<?>> MAPPERS = Arrays.asList(
            new PersonNameMapper(),
            new AcceptanceTypeMapper(),
            new AcceptanceCodeMapper(),
            new PersonSexMapper()
    );

//    public Interchange map(Parameters parameters) {
//        List<?> collect = MAPPERS.stream()
//            .map(mapper -> mapper.map(parameters))
//            .collect(Collectors.toList());
//
//        return new Interchange().setSegments();
//    }
}
