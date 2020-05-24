package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateDAO;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FhirToEdifactServiceTest {

    private static final String NHS_NUMBER = "54321";
    private static final String GP_CODE = "GP123";
    private static final String HA_CODE = "HA456";
    private static final Long SIS = 45L;
    private static final Long SMS = 56L;
    private static final Long TN = 5174L;

    @Mock
    OutboundStateRepository outboundStateRepository;

    @Mock
    SequenceService sequenceService;

    @Mock
    TimestampService timestampService;

    @InjectMocks
    FhirToEdifactService fhirToEdifactService;

    private ZonedDateTime expectedTimestamp;

    @BeforeEach
    public void beforeEach() {
        when(sequenceService.generateMessageId(GP_CODE, HA_CODE)).thenReturn(SMS);
        when(sequenceService.generateInterchangeId(GP_CODE, HA_CODE)).thenReturn(SIS);
        when(sequenceService.generateTransactionId()).thenReturn(TN);
        expectedTimestamp = ZonedDateTime.of(2020, 4, 27, 17, 37, 0, 0, UTC);
        when(timestampService.getCurrentTimestamp()).thenReturn(expectedTimestamp);
    }

    @Test
    public void when_convertedSuccessfully_dependenciesCalledCorrectly() throws Exception {
        Patient patient = createPatient();
        String operationId = UUID.randomUUID().toString();

        fhirToEdifactService.convertToEdifact(patient, operationId, ReferenceTransactionType.TransactionType.ACCEPTANCE);

        verify(sequenceService).generateInterchangeId(GP_CODE, HA_CODE);
        verify(sequenceService).generateMessageId(GP_CODE, HA_CODE);
        verify(sequenceService).generateTransactionId();
        verify(timestampService).getCurrentTimestamp();

        OutboundStateDAO expected = new OutboundStateDAO();
        expected.setRecipient(HA_CODE);
        expected.setSender(GP_CODE);
        expected.setSendInterchangeSequence(SIS);
        expected.setSendMessageSequence(SMS);
        expected.setTransactionId(TN);
        expected.setTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE.getAbbreviation());
        expected.setTransactionTimestamp(Date.from(expectedTimestamp.toInstant()));
        expected.setOperationId(operationId);
        verify(outboundStateRepository).save(expected);
    }

    @Test
    public void when_convertedSuccessfully_edifactIsCorrect() throws Exception {
        Patient patient = createPatient();
        String operationId = UUID.randomUUID().toString();

        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(patient, operationId, ReferenceTransactionType.TransactionType.ACCEPTANCE);

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

        Identifier gpId = new Identifier();
        gpId.setValue(GP_CODE);
        Reference gpRef = new Reference();
        gpRef.setIdentifier(gpId);
        patient.setGeneralPractitioner(singletonList(gpRef));

        Identifier haId = new Identifier();
        haId.setValue(HA_CODE);
        Reference haRef = new Reference();
        haRef.setIdentifier(haId);
        patient.setManagingOrganization(haRef);
        return patient;
    }

}
