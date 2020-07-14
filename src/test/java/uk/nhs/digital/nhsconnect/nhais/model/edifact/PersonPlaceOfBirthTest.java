package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonPlaceOfBirthTest {

    @Test
    void getKey() {
        assertThat(new PersonPlaceOfBirth("locationXYZ").getKey()).isEqualTo("LOC");
    }

    @Test
    void getValue() {
        assertThat(new PersonPlaceOfBirth("locationXYZ").getValue()).isEqualTo("950+locationXYZ");
    }

    @Test
    void preValidate() {
        assertThatCode(() -> new PersonPlaceOfBirth("locationXYZ").preValidate()).doesNotThrowAnyException();
        assertThatThrownBy(() -> new PersonPlaceOfBirth(null).preValidate()).isInstanceOf(EdifactValidationException.class);
    }
}