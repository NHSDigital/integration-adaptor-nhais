package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

/**
 * Delegates to a translator based on transaction type and other information
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirToEdifactManager {

    private final AcceptanceBirthTranslator acceptanceBirthTranslator;
    private final AcceptanceImmigrantTranslator acceptanceImmigrantTranslator;
    private final StubTranslator stubTranslator;

    public List<Segment> createMessageSegments(Parameters parameters, ReferenceTransactionType.TransactionType transactionType) throws FhirValidationException {
        switch (transactionType) {
            case ACCEPTANCE:
                return delegateAcceptance(parameters);
            default:
                return stubTranslator.translate(parameters);
        }
    }

    private List<Segment> delegateAcceptance(Parameters parameters) throws FhirValidationException {
        var acceptanceType = ParametersExtension.extractAcceptanceType(parameters);
        switch (acceptanceType) {
            case BIRTH:
                return acceptanceBirthTranslator.translate(parameters);
            case FIRST:
            case TRANSFER_IN:
            case IMMIGRANT:
                return acceptanceImmigrantTranslator.translate(parameters);
            default:
                throw new UnsupportedOperationException(String.format("%s is not supported", acceptanceType));
        }
    }
}
