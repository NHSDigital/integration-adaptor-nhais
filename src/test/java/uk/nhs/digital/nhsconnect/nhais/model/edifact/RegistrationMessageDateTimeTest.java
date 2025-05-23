package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RegistrationMessageDateTimeTest {

    private static final Instant WINTER = ZonedDateTime
        .of(2020, 3, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private static final Instant SUMMER = ZonedDateTime
        .of(2020, 5, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();

    @Test
    public void When_ToEdifactAndInstantInWinter_Expect_EdifactIsUTC() throws EdifactValidationException {
        assertThat(new RegistrationMessageDateTime(WINTER).toEdifact()).isEqualTo("DTM+137:202003282058:203'");
    }

    @Test
    public void When_ToEdifactAndInstantInSummer_Expect_EdifactIsBST() throws EdifactValidationException {
        // the translated times are UK local time / BST and one hour "ahead" of UTC
        assertThat(new RegistrationMessageDateTime(SUMMER).toEdifact()).isEqualTo("DTM+137:202005282158:203'");
    }

    @Test
    void When_FromStringAndEdifactIsWinterUTC_Expect_InstantIsUTC() {
        assertThat(RegistrationMessageDateTime.fromString("DTM+137:202003282058:203'").getTimestamp()).isEqualTo(WINTER);
    }

    @Test
    void When_FromStringAndEdifactIsSummerBST_Expect_InstantIsUTC() {
        // the internal Instant representation (UTC) is one hour "behind" the EDIFACT timestamp which is UK local time / BST
        assertThat(RegistrationMessageDateTime.fromString("DTM+137:202005282158:203'").getTimestamp()).isEqualTo(SUMMER);
    }

    @Test
    void When_FromStringAndStringIsNotDTMSegment_Expect_ThrowsException() {
        assertThatThrownBy(() -> RegistrationMessageDateTime.fromString("ABC+123:456:789'")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}