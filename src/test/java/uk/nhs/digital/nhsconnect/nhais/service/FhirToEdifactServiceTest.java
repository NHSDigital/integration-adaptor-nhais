package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.GeneralPractitionerIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.GpTradingPartnerCode;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;
import uk.nhs.digital.nhsconnect.nhais.translator.FhirToEdifactSegmentTranslator;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FhirToEdifactServiceTest {

    private static final String OPERATION_ID = "536f8ae2df897ae1d351a2aaed5c6f731f44718b8db412ee073e5def5bd3cef0";
    private static final String NHS_NUMBER = "54321";
    private static final String GP_TRADING_PARTNER_CODE = "GP123";
    private static final String HA_CIPHER = "HA4";
    private static final String GP_CODE = "X11";
    private static final String HA_TRADING_PARTNER_CODE = HA_CIPHER + "1";
    private static final Long SIS = 45L;
    private static final Long SMS = 56L;
    private static final Long TN = 5174L;

    @Mock
    OutboundStateRepository outboundStateRepository;

    @Mock
    SequenceService sequenceService;

    @Mock
    TimestampService timestampService;

    @Mock
    FhirToEdifactSegmentTranslator fhirToEdifactSegmentTranslator;

    @InjectMocks
    FhirToEdifactService fhirToEdifactService;

    private Instant expectedTimestamp;

    @BeforeEach
    public void beforeEach() throws Exception {
        when(sequenceService.generateMessageId(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE)).thenReturn(SMS);
        when(sequenceService.generateInterchangeId(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE)).thenReturn(SIS);
        when(sequenceService.generateTransactionId(GP_TRADING_PARTNER_CODE)).thenReturn(TN);
        expectedTimestamp = ZonedDateTime
            .of(2020, 4, 27, 17, 37, 0, 0, TimestampService.UKZone)
            .toInstant();
        when(timestampService.getCurrentTimestamp()).thenReturn(expectedTimestamp);
        // segments related to state management only
        when(fhirToEdifactSegmentTranslator.createMessageSegments(any(), any())).thenReturn(Arrays.asList(
            new BeginningOfMessage(),
            new NameAndAddress(HA_CIPHER, NameAndAddress.QualifierAndCode.FHS),
            new DateTimePeriod(expectedTimestamp, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP),
            new ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE),
            new SegmentGroup(1),
            new ReferenceTransactionNumber()
        ));
    }

    @Test
    public void when_convertedSuccessfully_dependenciesCalledCorrectly() throws Exception {
        Parameters patient = createPatient();

        fhirToEdifactService.convertToEdifact(patient, ReferenceTransactionType.TransactionType.ACCEPTANCE);

        verify(sequenceService).generateInterchangeId(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE);
        verify(sequenceService).generateMessageId(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE);
        verify(sequenceService).generateTransactionId(GP_TRADING_PARTNER_CODE);
        verify(timestampService).getCurrentTimestamp();

        OutboundState expected = new OutboundState();
        expected.setWorkflowId(WorkflowId.REGISTRATION);
        expected.setRecipient(HA_TRADING_PARTNER_CODE);
        expected.setSender(GP_TRADING_PARTNER_CODE);
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

        String expected = "UNB+UNOA:2+GP123+HA41+200427:1737+00000045'\n" +
            "UNH+00000056+FHSREG:0:1:FH:FHS001'\n" +
            "BGM+++507'\n" +
            "NAD+FHS+HA4:954'\n" +
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
        patient.setIdentifier(singletonList(new NhsIdentifier(NHS_NUMBER)));
        patient.setGeneralPractitioner(
            singletonList(
                new Reference().setIdentifier(new GeneralPractitionerIdentifier(GP_CODE))
            )
        );
        patient.setManagingOrganization(new Reference().setIdentifier(new ManagingOrganizationIdentifier(HA_CIPHER)));

        return new Parameters()
            .addParameter(new PatientParameter(patient))
            .addParameter(new GpTradingPartnerCode(GP_TRADING_PARTNER_CODE));
    }

}
