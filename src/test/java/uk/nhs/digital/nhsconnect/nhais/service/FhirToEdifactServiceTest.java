package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FhirToEdifactServiceTest {

    private static final String NHS_NUMBER = "54321";
    private static final String GP_CODE = "GP123";
    private static final String HA_CODE = "HA456";
    private static final String SIS = "45";
    private static final String SMS = "56";
    private static final String TN = "5174";

    private static final Pattern UNB = Pattern.compile("^UNB\\+UNOA:2\\+(?<sender>[a-zA-Z0-9]+)\\+(?<recipient>[a-zA-Z0-9]+)+\\+(?<timestamp>[0-9]{6}:[0-9]{4})\\+(?<sis>[0-9]{8})'$");

    @Mock
    OutboundStateRepository outboundStateRepository;

    @Mock
    SequenceService sequenceService;

    @InjectMocks
    FhirToEdifactService fhirToEdifactService;

    @Test @Disabled
    public void when_convertedSuccessfully_dependenciesCalledCorrectly() {
        Patient patient = createPatient();
        String operationId = UUID.randomUUID().toString();

        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(patient, operationId, null);

        verify(sequenceService).generateInterchangeId(GP_CODE, HA_CODE);
        verify(sequenceService).generateMessageId(GP_CODE, HA_CODE);
        verify(outboundStateRepository).save(argThat(outboundState ->
            outboundState.getRecipient().equals(HA_CODE) &&
                    outboundState.getSender().equals(GP_CODE) &&
                    outboundState.getSendInterchangeSequence().equals(SIS) &&
                    outboundState.getSendMessageSequence().equals(SMS) &&
                    outboundState.getTransactionId().equals(TN) &&
                    outboundState.getOperationId().equals(operationId) &&
                    outboundState.getTransactionTimestamp() != null
        ));
    }

    @Test @Disabled
    public void when_convertedSuccessfully_edifactIsCorrect() {
        Patient patient = createPatient();
        String operationId = UUID.randomUUID().toString();

        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(patient, operationId, null);

        String expected = "UNB+UNOA:2+GP123+HA456+200427:1737+00000045'\n" +
                "UNH+00000056+FHSREG:0:1:FH:FHS001'\n" +
                "BGM+++507'\n" +
                "NAD+FHS+HA456:954'\n" +
                "DTM+137:202004271737:203'\n" +
                "RFF+950:G1'\n" +
                "S01+1'\n" +
                "RFF+TN:5174'\n" +
                "UNT+8+00000056'\n" +
                "UNZ+1+00000045'";

        assertEquals(expected, translatedInterchange.getEdifact());
    }

    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setId(NHS_NUMBER);
        Identifier patientId = new Identifier();
        patientId.setValue(NHS_NUMBER);
        patient.setIdentifier(singletonList(patientId));
        Organization gp = new Organization();
        Identifier gpId = new Identifier();
        gpId.setValue(GP_CODE);
        gp.setIdentifier(singletonList(gpId));
        Reference gpRef = new Reference(gp);
        patient.setGeneralPractitioner(singletonList(gpRef));
        Organization ha = new Organization();
        Identifier haId = new Identifier();
        haId.setValue(HA_CODE);
        ha.setIdentifier(singletonList(haId));
        Reference haRef = new Reference(ha);
        patient.setManagingOrganization(haRef);
        return patient;
    }

}
