package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentFhirToEdifactTranslatorFactory {

    private final AmendmentNameToEdifactTranslator amendmentNameToEdifactTranslator;
    private final AmendmentPreviousNameToEdifactTranslator amendmentPreviousNameToEdifactTranslator;

    public List<AmendmentToEdifactTranslator> getTranslators() {
        return List.of(
            amendmentNameToEdifactTranslator,
            amendmentPreviousNameToEdifactTranslator
        );
    }
}
