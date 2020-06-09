package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FhirToEdifactServiceTest {
    private static final Instant FIXED_TIME = ZonedDateTime.of(
            1991,
            11,
            6,
            23,
            55,
            0,
            0,
            ZoneId.of("Europe/London")).toInstant();

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
        Parameters parameters = createParameters();

        fhirToEdifactService.convertToEdifact(parameters, ReferenceTransactionType.TransactionType.ACCEPTANCE);

        verify(sequenceService).generateInterchangeId(GP_CODE, HA_CODE);
        verify(sequenceService).generateMessageId(GP_CODE, HA_CODE);
        verify(sequenceService).generateTransactionId();
        verify(timestampService).getCurrentTimestamp();

        OutboundState expected = new OutboundState();
        expected.setRecipient(HA_CODE);
        expected.setSender(GP_CODE);
        expected.setSendInterchangeSequence(SIS);
        expected.setSendMessageSequence(SMS);
        expected.setTransactionId(TN);
        expected.setTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE.getAbbreviation());
        expected.setTransactionTimestamp(Date.from(expectedTimestamp));
        expected.setOperationId(OPERATION_ID);
        verify(outboundStateRepository).save(expected);
    }

    @Test
    public void when_convertedSuccessfully_edifactIsCorrect() {
        Parameters parameters = createParameters();

        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(parameters, ReferenceTransactionType.TransactionType.ACCEPTANCE);

        String expected = "UNB+UNOA:2+GP123+HA456+200427:1737+00000045'\n" +
                "UNH+00000056+FHSREG:0:1:FH:FHS001'\n" +
                "BGM+++507'\n" +
                "S01+1'\n" +
                "RFF+TN:5174'\n" +
                "NAD+FHS+HA456:954'\n" +
                "NAD+GP+GP123,281:900'\n" +
                "HEA+ACD+S:ZZZ'\n" +
                "HEA+ATP+1:ZZZ'\n" +
                "PNA+PAT+54321:OPI+++SU:FamilyName++++'\n" +
                "DTM+329:19911106:102'\n" +
                "PDI+Female'\n" +
                "NAD+PAT++534 EREWHON ST PEASANTVILLE:RAINBOW:VIC  3999'\n" +
                "NAD+PAT++31 TEST ST PEASANTVILLE:TEST-RAINBOW:VIC  3999'\n" +
                "DTM+957:19911106:102'\n" +
                "NAD+PGP+Old-One,281:900'\n" +
                "UNT+8+00000056'\n" +
                "UNZ+1+00000045'";

        assertThat(translatedInterchange.getEdifact()).isEqualTo(expected);
    }

    private Parameters createParameters() {
        Patient patient = new Patient();
        patient.setId(NHS_NUMBER);
        Identifier patientId = new Identifier();
        patientId.setValue(NHS_NUMBER);
        patientId.setSystem("https://fhir.nhs.uk/Id/nhs-number");
        patient.setIdentifier(singletonList(patientId));

        Reference gpRef = new Reference();
        gpRef.setReference("Practitioner/" + GP_CODE);
        patient.setGeneralPractitioner(singletonList(gpRef));

        Reference haRef = new Reference();
        haRef.setReference("Organization/" + HA_CODE);
        patient.setManagingOrganization(haRef);

        patient.setGender(Enumerations.AdministrativeGender.FEMALE);

        HumanName humanName = new HumanName();
        humanName.setFamily("FamilyName");
        patient.setName(singletonList(humanName));

        Address address = new Address();
        address.setUse(Address.AddressUse.HOME);
        address.setText("534 Erewhon St PeasantVille, Rainbow, Vic  3999");
        address.setLine(List.of(new StringType("534 Erewhon St")));

        Address oldAddress = new Address();
        oldAddress.setUse(Address.AddressUse.OLD);
        oldAddress.setText("31 Test St PeasantVille, test-Rainbow, Vic  3999");
        oldAddress.setLine(List.of(new StringType("534 Erewhon St")));
        patient.setAddress(List.of(address, oldAddress));

        patient.setBirthDate(java.sql.Date.from(FIXED_TIME));

        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName("patient")
                .setResource(patient);
        parameters.addParameter()
                .setName("acceptanceType")
                .setValue(new StringType("birth"));
        parameters.addParameter()
                .setName("acceptanceCode")
                .setValue(new StringType("S"));
        parameters.addParameter()
                .setName("entryDate")
                .setValue(new StringType(FIXED_TIME.toString()));
        parameters.addParameter()
                .setName("previousGPName")
                .setValue(new StringType("Practitioner/Old-One"));

        return parameters;
    }
}
