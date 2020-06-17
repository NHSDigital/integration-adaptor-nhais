package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FhirToEdifactServiceTest {

    private static final String OPERATION_ID = "4297001d94b41d2e604059879d45123880760cb0262d28f85394a08de5e761b8";
    private static final String NHS_NUMBER = "54321";
    private static final String GP_CODE = "GP123";
    private static final String HA_CODE = "HA456";
    private static final Long SIS = 45L;
    private static final Long SMS = 56L;
    private static final Long TN = 5174L;

    @Spy
    FhirParser fhirParser;

    @Mock
    OutboundStateRepository outboundStateRepository;

    @Mock
    SequenceService sequenceService;

    @Mock
    TimestampService timestampService;

    @InjectMocks
    FhirToEdifactService fhirToEdifactService;

    private Instant expectedTimestamp;

    @BeforeEach
    public void beforeEach() {
        when(sequenceService.generateMessageId(GP_CODE, HA_CODE)).thenReturn(SMS);
        when(sequenceService.generateInterchangeId(GP_CODE, HA_CODE)).thenReturn(SIS);
        when(sequenceService.generateTransactionId()).thenReturn(TN);
        expectedTimestamp = ZonedDateTime
            .of(2020, 4, 27, 17, 37, 0, 0, TimestampService.UKZone)
            .toInstant();
        when(timestampService.getCurrentTimestamp()).thenReturn(expectedTimestamp);
    }

    @Test
    public void when_convertedSuccessfully_dependenciesCalledCorrectly() throws Exception {
        Parameters patient = createPatient();

        fhirToEdifactService.convertToEdifact(patient, ReferenceTransactionType.TransactionType.ACCEPTANCE);

        verify(sequenceService).generateInterchangeId(GP_CODE, HA_CODE);
        verify(sequenceService).generateMessageId(GP_CODE, HA_CODE);
        verify(sequenceService).generateTransactionId();
        verify(timestampService).getCurrentTimestamp();

        OutboundState expected = new OutboundState();
        expected.setWorkflowId(WorkflowId.REGISTRATION);
        expected.setRecipient(HA_CODE);
        expected.setSender(GP_CODE);
        expected.setSendInterchangeSequence(SIS);
        expected.setSendMessageSequence(SMS);
        expected.setTransactionId(TN);
        expected.setTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE.getAbbreviation());
        expected.setTransactionTimestamp(expectedTimestamp);
        expected.setOperationId(OPERATION_ID);
        verify(outboundStateRepository).save(expected);
    }

    @Test
    public void when_convertedSuccessfully_edifactIsCorrect() throws Exception {
        Parameters patient = createPatient();

        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(patient, ReferenceTransactionType.TransactionType.ACCEPTANCE);

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

        assertThat(translatedInterchange.getEdifact()).isEqualTo(expected);
    }

    private Parameters createPatient() {
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

        Parameters parameters = new Parameters();
        Parameters.ParametersParameterComponent param = new Parameters.ParametersParameterComponent();
        param.setName("patient");
        param.setResource(patient);
        parameters.addParameter(param);
        return parameters;
    }

}
