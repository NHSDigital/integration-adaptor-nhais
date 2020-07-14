package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrugsMarkerTest {

    @Test
    void toEdifact() {
        DrugsMarker trueDrugsMarker = new DrugsMarker(true);
        DrugsMarker falseDrugsMarker = new DrugsMarker(false);

        assertThat(trueDrugsMarker.toEdifact()).isEqualTo("HEA+DM+Y:ZZZ'");
        assertThat(falseDrugsMarker.toEdifact()).isEqualTo("HEA+DM+%:ZZZ'");
    }

    @Test
    void getKey() {
        DrugsMarker drugsMarker = new DrugsMarker(true);
        assertThat(drugsMarker.getKey()).isEqualTo("HEA");
    }

    @Test
    void getValue() {
        DrugsMarker drugsMarker = new DrugsMarker(true);
        assertThat(drugsMarker.getValue()).isEqualTo("DM+Y:ZZZ");
    }
}