package uk.nhs.digital.nhsconnect.nhais.outbound;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.outbound.jsonpatch.JsonPatchTranslationItems;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirTranslationItems;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TranslationItems implements JsonPatchTranslationItems, FhirTranslationItems {
    private Parameters parameters;
    private AmendmentBody amendmentBody;
    private ReferenceTransactionType.Outbound transactionType;
    private List<Segment> segments = new ArrayList<>();
    private String sender;
    private String recipient;
    private String operationId;
    private Long sendMessageSequence;
    private Long sendInterchangeSequence;
    private Long transactionNumber;
    private Instant translationTimestamp;
}
