package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

public interface FromFhirToEdifactMapper<T extends Segment> {
    static <T extends Segment> FromFhirToEdifactMapper<T> emptyMapper(T segment) {
        return parameters -> segment;
    }

    static FromFhirToEdifactMapper<?> optional(OptionalFromFhirToEdifactMapper<?> mapper, Parameters parameters) {
        if (mapper.canMap(parameters)) {
            return mapper;
        }
        return new FromFhirToEdifactMapper.EmptyMapper();
    }

    T map(Parameters parameters);

    class EmptyMapper implements FromFhirToEdifactMapper<Segment> {

        @Override
        public Segment map(Parameters parameters) {
            return null;
        }
    }
}