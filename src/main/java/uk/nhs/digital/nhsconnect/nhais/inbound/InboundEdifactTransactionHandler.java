package uk.nhs.digital.nhsconnect.nhais.inbound;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.EdifactToFhirService;
import uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.EdifactToPatchService;
import uk.nhs.digital.nhsconnect.nhais.inbound.queue.InboundGpSystemService;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InboundEdifactTransactionHandler {
    private final EdifactToFhirService edifactToFhirService;
    private final EdifactToPatchService edifactToPatchService;

    public InboundGpSystemService.DataToSend translate(Transaction transaction) {
        var transactionType = transaction.getMessage().getReferenceTransactionType().getTransactionType();

        if (ReferenceTransactionType.Inbound.AMENDMENT.equals(transactionType)) {
            return new InboundGpSystemService.DataToSend()
                .setContent(edifactToPatchService.convertToPatch(transaction));
        }

        return new InboundGpSystemService.DataToSend()
            .setContent(edifactToFhirService.convertToFhir(transaction));
    }
}
