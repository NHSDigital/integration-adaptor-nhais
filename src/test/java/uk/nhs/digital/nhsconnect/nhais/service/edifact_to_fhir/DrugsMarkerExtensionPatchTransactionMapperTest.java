package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DrugsMarkerExtensionPatchTransactionMapperTest {

    private static DrugsMarkerExtensionPatchTransactionMapper drugsMarkerExtensionPatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private DrugsMarker drugsMarker;

    @BeforeAll
    public static void setUp() {
        drugsMarkerExtensionPatchTransactionMapper = new DrugsMarkerExtensionPatchTransactionMapper();
    }


    @Test
    void whenDrugsMarkerIsTrue_thenMapIntoTrue() {
        when(transaction.getDrugsMarker()).thenReturn(Optional.of(drugsMarker));
        when(drugsMarker.isDrugsMarker()).thenReturn(true);

        AmendmentPatch amendmentPatch = drugsMarkerExtensionPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo("true");
    }

    @Test
    void whenDrugsMarkerIsFalse_thenMapIntoFalse() {
        when(transaction.getDrugsMarker()).thenReturn(Optional.of(drugsMarker));
        when(drugsMarker.isDrugsMarker()).thenReturn(false);

        AmendmentPatch amendmentPatch = drugsMarkerExtensionPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo("false");
    }

    @Test
    void whenNoDrugsMarker_thenReturnNullAmendmentPatch() {
        when(transaction.getDrugsMarker()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = drugsMarkerExtensionPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}