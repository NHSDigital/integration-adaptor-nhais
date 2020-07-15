package uk.nhs.digital.nhsconnect.nhais.translator.acceptance;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonPlaceOfBirthMapper;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionalInputValidator {

    public boolean placeOfBirthIsMissing(Parameters parameters) {
        return !new PersonPlaceOfBirthMapper().canMap(parameters);
    }

    public boolean nhsNumberIsMissing(Parameters parameters) {
        return StringUtils.isBlank(new PersonNameMapper().map(parameters).getNhsNumber());
    }
}
