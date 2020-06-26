package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.TransactionMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EdifactToFhirServiceTest {

    private final String rejectionMessage = "UNB+UNOA:2+TES5+XX11+020114:1619+00000003'\n" +
        "UNH+00000004+FHSREG:0:1:FH:FHS001'\n" +
        "BGM+++507'\n" +
        "NAD+FHS+XX1:954'\n" +
        "DTM+137:199201141619:203'\n" +
        "RFF+950:F3'\n" +
        "S01+1'\n" +
        "RFF+TN:18'\n" +
        "NAD+GP+2750922,295:900'\n" +
        "NAD+RIC+RT:956'\n" +
        "QTY+951:6'\n" +
        "QTY+952:3'\n" +
        "HEA+ACD+A:ZZZ'\n" +
        "HEA+ATP+2:ZZZ'\n" +
        "HEA+BM+S:ZZZ'\n" +
        "HEA+DM+Y:ZZZ'\n" +
        "DTM+956:19920114:102'\n" +
        "LOC+950+GLASGOW'\n" +
        "FTX+RGI+++BABY AT THE REYNOLDS-THORPE CENTRE'\n" +
        "S02+2'\n" +
        "PNA+PAT+NHS123:OPI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'\n" +
        "DTM+329:19911209:102'\n" +
        "PDI+2'\n" +
        "NAD+PAT++??:26 FARMSIDE CLOSE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7ET'\n" +
        "UNT+24+00000004'\n" +
        "UNZ+1+00000003'";

    private final String approvalMessage = "UNB+UNOA:2+TES5+XX11+020114:1619+00000003'\n" +
        "UNH+00000004+FHSREG:0:1:FH:FHS001'\n" +
        "BGM+++507'\n" +
        "NAD+FHS+XX1:954'\n" +
        "DTM+137:199201141619:203'\n" +
        "RFF+950:F4'\n" +
        "S01+1'\n" +
        "RFF+TN:18'\n" +
        "NAD+GP+2750922,295:900'\n" +
        "NAD+RIC+RT:956'\n" +
        "QTY+951:6'\n" +
        "QTY+952:3'\n" +
        "HEA+ACD+A:ZZZ'\n" +
        "HEA+ATP+2:ZZZ'\n" +
        "HEA+BM+S:ZZZ'\n" +
        "HEA+DM+Y:ZZZ'\n" +
        "DTM+956:19920114:102'\n" +
        "LOC+950+GLASGOW'\n" +
        "FTX+RGI+++BABY AT THE REYNOLDS-THORPE CENTRE'\n" +
        "S02+2'\n" +
        "PNA+PAT+NHS123:OPI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'\n" +
        "DTM+329:19911209:102'\n" +
        "PDI+2'\n" +
        "NAD+PAT++??:26 FARMSIDE CLOSE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7ET'\n" +
        "UNT+24+00000004'\n" +
        "UNZ+1+00000003'";
    @Mock
    private TransactionMapper rejectionMapper;
    @Mock
    private TransactionMapper acceptanceMapper;
    @Mock
    private TransactionMapper approvalMapper;

    private Map<ReferenceTransactionType.TransactionType, TransactionMapper> transactionMappers;

    @BeforeEach
    void setUp() {
        transactionMappers = Map.of(
            ReferenceTransactionType.TransactionType.REJECTION, rejectionMapper,
            ReferenceTransactionType.TransactionType.ACCEPTANCE, acceptanceMapper,
            ReferenceTransactionType.TransactionType.APPROVAL, approvalMapper);
    }

    @Test
    void convertRejectionToFhir() {
        Parameters parameters = new EdifactToFhirService(transactionMappers)
            .convertToFhir(new EdifactParser().parse(rejectionMessage).getMessages().get(0).getTransactions().get(0));

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        Patient patient = parametersExt.extractPatient();
        String gpTradingPartnerCode = parametersExt.extractValue("gpTradingPartnerCode");

        assertThat(patient.getManagingOrganization().getIdentifier().getSystem())
            .isEqualTo("https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation");
        assertThat(patient.getManagingOrganization().getIdentifier().getValue()).isEqualTo("XX1");

        assertThat(patient.getGeneralPractitionerFirstRep().getIdentifier().getSystem())
            .isEqualTo("https://fhir.hl7.org.uk/Id/gmc-number");
        assertThat(patient.getGeneralPractitionerFirstRep().getIdentifier().getValue()).isEqualTo("2750922,295");
        assertThat(gpTradingPartnerCode).isEqualTo("XX11");

        verify(acceptanceMapper, never()).map(any(), any());
        verify(approvalMapper, never()).map(any(), any());
        verify(rejectionMapper).map(any(), any());
    }

    @Test
    void convertApprovalToFhir() {
        Parameters parameters = new EdifactToFhirService(transactionMappers)
            .convertToFhir(new EdifactParser().parse(approvalMessage).getMessages().get(0).getTransactions().get(0));

        ParametersExtension parametersExt = new ParametersExtension(parameters);

        Patient patient = parametersExt.extractPatient();
        String gpTradingPartnerCode = parametersExt.extractValue("gpTradingPartnerCode");

        assertThat(patient.getManagingOrganization().getIdentifier().getSystem())
            .isEqualTo("https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation");
        assertThat(patient.getManagingOrganization().getIdentifier().getValue()).isEqualTo("XX1");

        assertThat(patient.getGeneralPractitionerFirstRep().getIdentifier().getSystem())
            .isEqualTo("https://fhir.hl7.org.uk/Id/gmc-number");
        assertThat(patient.getGeneralPractitionerFirstRep().getIdentifier().getValue()).isEqualTo("2750922,295");
        assertThat(gpTradingPartnerCode).isEqualTo("XX11");

        verify(acceptanceMapper, never()).map(any(), any());
        verify(rejectionMapper, never()).map(any(), any());
        verify(approvalMapper).map(any(), any());
    }
}