package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.List;

/**
 * Delegates to a translator based on transaction type and other information
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirToEdifactManager {

    private final AcceptanceBirthTranslator acceptanceBirthTranslator;
    private final StubTranslator stubTranslator;

    public List<Segment> createMessageSegments(Parameters parameters, ReferenceTransactionType.TransactionType transactionType) throws FhirValidationException {
        switch (transactionType) {
// TODO: enable this and remove stub once acceptance implemented
//            case ACCEPTANCE:
//                return delegateAcceptance(transactionItems);
            default:
                return stubTranslator.translate(parameters);
        }
    }

    private List<Segment> delegateAcceptance(Parameters parameters) throws FhirValidationException {
        String acceptanceType = determineAcceptanceType(parameters);
        if("birth".equals(acceptanceType)) {
            return acceptanceBirthTranslator.translate(parameters);
        }
        return null;
    }

    private String determineAcceptanceType(Parameters parameters) {
        return "birth";
    }

}
