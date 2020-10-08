package uk.nhs.digital.nhsconnect.nhais.outbound.fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.GpTradingPartnerCode;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.PatientParameter;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshCypherDecoder;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RegistrationMessageDateTime;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.GeneralPractitionerIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.FhirToEdifactSegmentTranslator;
import uk.nhs.digital.nhsconnect.nhais.sequence.SequenceService;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
    private static final String CONVERSATION_ID = "asdf1234";

    @Mock
    private OutboundStateRepository outboundStateRepository;

    @Mock
    private SequenceService sequenceService;

    @Mock
    private TimestampService timestampService;

    @Mock
    private FhirToEdifactSegmentTranslator fhirToEdifactSegmentTranslator;

    @Mock
    private MeshCypherDecoder meshCypherDecoder;

    @Mock
    private ConversationIdService conversationIdService;

    @InjectMocks
    private FhirToEdifactService fhirToEdifactService;

    private Instant expectedTimestamp;

    @BeforeEach
    public void beforeEach() {
        doNothing().when(meshCypherDecoder).validateRecipient(any());
        when(sequenceService.generateMessageSequence(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE)).thenReturn(SMS);
        when(sequenceService.generateInterchangeSequence(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE)).thenReturn(SIS);
        when(sequenceService.generateTransactionNumber(GP_TRADING_PARTNER_CODE)).thenReturn(TN);
        expectedTimestamp = ZonedDateTime
            .of(2020, 4, 27, 17, 37, 0, 0, TimestampService.UKZone)
            .toInstant();
        when(timestampService.getCurrentTimestamp()).thenReturn(expectedTimestamp);
        // segments related to state management only
        when(fhirToEdifactSegmentTranslator.createMessageSegments(any(), any())).thenReturn(Arrays.asList(
            new BeginningOfMessage(),
            new NameAndAddress(HA_CIPHER, NameAndAddress.QualifierAndCode.FHS),
            new RegistrationMessageDateTime().setTimestamp(expectedTimestamp),
            new ReferenceTransactionType(ReferenceTransactionType.Outbound.ACCEPTANCE),
            new SegmentGroup(1),
            new ReferenceTransactionNumber()
        ));
        when(conversationIdService.getCurrentConversationId()).thenReturn(CONVERSATION_ID);
    }

    @Test
    public void when_convertedSuccessfully_dependenciesCalledCorrectly() {
        Parameters patient = createPatient();

        fhirToEdifactService.convertToEdifact(patient, ReferenceTransactionType.Outbound.ACCEPTANCE);

        verify(sequenceService).generateInterchangeSequence(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE);
        verify(sequenceService).generateMessageSequence(GP_TRADING_PARTNER_CODE, HA_TRADING_PARTNER_CODE);
        verify(sequenceService).generateTransactionNumber(GP_TRADING_PARTNER_CODE);
        verify(timestampService).getCurrentTimestamp();

        OutboundState expected = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setRecipient(HA_TRADING_PARTNER_CODE)
            .setSender(GP_TRADING_PARTNER_CODE)
            .setInterchangeSequence(SIS)
            .setMessageSequence(SMS)
            .setTransactionNumber(TN)
            .setTransactionType(ReferenceTransactionType.Outbound.ACCEPTANCE)
            .setTranslationTimestamp(expectedTimestamp)
            .setOperationId(OPERATION_ID)
            .setConversationId(CONVERSATION_ID);
        verify(outboundStateRepository).save(expected);
    }

    @Test
    public void when_convertedSuccessfully_edifactIsCorrect() {
        Parameters patient = createPatient();

        OutboundMeshMessage meshMessage = fhirToEdifactService.convertToEdifact(patient, ReferenceTransactionType.Outbound.ACCEPTANCE);

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

        assertThat(meshMessage.getContent()).isEqualTo(expected);
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
