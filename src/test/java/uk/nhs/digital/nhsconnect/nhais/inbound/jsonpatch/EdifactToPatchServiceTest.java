package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper.PatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdifactToPatchServiceTest {

    private static final String GP_CODE = "some_gp_code";
    private static final String GP_TRADING_PARTNER_CODE = "some_recipient";
    private static final String HEALTHCARE_PARTY_CODE = "some_healthcare_party_code";
    private static final String NHS_NUMBER = "some_nhs_number";

    private final AmendmentPatch amendmentPatch1 = new AmendmentPatch();
    private final AmendmentPatch amendmentPatch2 = new AmendmentPatch();

    @Mock
    private PatchTransactionMapper patchTransactionMapper1;
    @Mock
    private PatchTransactionMapper patchTransactionMapper2;
    @Mock
    private Transaction transaction;
    @Mock
    private Message message;
    @Mock
    private Interchange interchange;

    private EdifactToPatchService edifactToPatchService;

    @BeforeEach
    void setUp() {
        amendmentPatch1.setPath("path1");
        amendmentPatch2.setPath("path2");
        amendmentPatch1.setOp(AmendmentPatchOperation.REMOVE);
        amendmentPatch2.setOp(AmendmentPatchOperation.ADD);
        when(patchTransactionMapper1.map(transaction)).thenReturn(amendmentPatch1);
        when(patchTransactionMapper2.map(transaction)).thenReturn(amendmentPatch2);

        when(transaction.getGpNameAndAddress()).thenReturn(new GpNameAndAddress(GP_CODE, ""));
        when(transaction.getPersonName()).thenReturn(Optional.of(PersonName.builder().nhsNumber(NHS_NUMBER).build()));
        when(transaction.getMessage()).thenReturn(message);
        when(message.getHealthAuthorityNameAndAddress()).thenReturn(new HealthAuthorityNameAndAddress(HEALTHCARE_PARTY_CODE, ""));
        when(message.getInterchange()).thenReturn(interchange);
        when(interchange.getInterchangeHeader()).thenReturn(new InterchangeHeader().setRecipient(GP_TRADING_PARTNER_CODE));

        edifactToPatchService = new EdifactToPatchService(List.of(patchTransactionMapper1, patchTransactionMapper2));
    }

    @Test
    void whenConvertingToPatch_properMandatoryFieldsAreSet() {
        var amendmentBody = edifactToPatchService.convertToPatch(transaction);

        assertThat(amendmentBody.getNhsNumber()).isEqualTo(NHS_NUMBER);
        assertThat(amendmentBody.getHealthcarePartyCode()).isEqualTo(HEALTHCARE_PARTY_CODE);
        assertThat(amendmentBody.getGpCode()).isEqualTo(GP_CODE);
        assertThat(amendmentBody.getGpTradingPartnerCode()).isEqualTo(GP_TRADING_PARTNER_CODE);
        assertThat(amendmentBody.getPatches()).containsExactly(amendmentPatch1, amendmentPatch2);

        verify(patchTransactionMapper1).map(transaction);
        verify(patchTransactionMapper2).map(transaction);
    }
}