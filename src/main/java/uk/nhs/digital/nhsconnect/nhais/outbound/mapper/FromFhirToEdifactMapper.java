package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface FromFhirToEdifactMapper<T extends Segment> {
    static <T extends Segment> FromFhirToEdifactMapper<T> mapSegment(T segment) {
        return parameters -> segment;
    }

    static FromFhirToEdifactMapper<?> optional(OptionalFromFhirToEdifactMapper<?> mapper, Parameters parameters) {
        if (mapper.inputDataExists(parameters)) {
            return mapper;
        }
        return new SkipMapper();
    }

    static FromFhirToEdifactMapper<?> optionalGroup(Segment startSegment, Collection<OptionalFromFhirToEdifactMapper<?>> mappers, Parameters parameters) {
        List<OptionalFromFhirToEdifactMapper<?>> mappableSegments = mappers
            .stream()
            .filter(mapper -> mapper.inputDataExists(parameters))
            .collect(Collectors.toList());

        if (mappableSegments.isEmpty()) {
            return new SkipMapper();
        }
        return mapSegment(startSegment);
    }

    T map(Parameters parameters);

    class SkipMapper implements FromFhirToEdifactMapper<Segment> {

        @Override
        public Segment map(Parameters parameters) {
            return null;
        }

    }
}