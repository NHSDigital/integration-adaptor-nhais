package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RegistrationTimestampTest {

    private final Instant WINTER = ZonedDateTime
        .of(2020, 3, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final Instant SUMMER = ZonedDateTime
        .of(2020, 5, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();

    @Test
    public void When_toEdifact_And_instantInWinter_Then_edifactIsUTC() throws EdifactValidationException {
        assertThat(new RegistrationTimestamp(WINTER).toEdifact()).isEqualTo("DTM+137:202003282058:203'");
    }

    @Test
    public void When_toEdifact_And_instantInSummer_Then_edifactIsBST() throws EdifactValidationException {
        // the translated times are UK local time / BST and one hour "ahead" of UTC
        assertThat(new RegistrationTimestamp(SUMMER).toEdifact()).isEqualTo("DTM+137:202005282158:203'");
    }

    @Test
    void When_fromString_And_edifactIsWinterUTC_Then_instantIsUTC() {
        assertThat(RegistrationTimestamp.fromString("DTM+137:202003282058:203'").getTimestamp()).isEqualTo(WINTER);
    }

    @Test
    void When_fromString_And_edifactIsSummerBST_Then_instantIsUTC() {
        // the internal Instant representation (UTC) is one hour "behind" the EDIFACT timestamp which is UK local time / BST
        assertThat(RegistrationTimestamp.fromString("DTM+137:202005282158:203'").getTimestamp()).isEqualTo(SUMMER);
    }

    @Test
    void When_fromString_And_stringIsNotDTMSegment_Then_throwsException() {
        assertThatThrownBy(() -> RegistrationTimestamp.fromString("ABC+123:456:789'")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}