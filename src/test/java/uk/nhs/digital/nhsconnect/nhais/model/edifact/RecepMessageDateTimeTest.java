package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RecepMessageDateTimeTest {

    private static final Instant WINTER = ZonedDateTime
        .of(LocalDateTime.parse("2020-03-28T20:58:00"), ZoneOffset.UTC)
        .toInstant();
    private static final Instant SUMMER = ZonedDateTime
        .of(LocalDateTime.parse("2020-05-28T20:58:00"), ZoneOffset.UTC)
        .toInstant();

    @Test
    public void When_ToEdifactAndInstantInWinter_Expect_EdifactIsUTC() throws EdifactValidationException {
        assertThat(new RecepMessageDateTime(WINTER).toEdifact()).isEqualTo("DTM+815:202003282058:306'");
    }

    @Test
    public void When_ToEdifactAndInstantInSummer_Expect_EdifactIsBST() throws EdifactValidationException {
        // the translated times are UK local time / BST and one hour "ahead" of UTC
        assertThat(new RecepMessageDateTime(SUMMER).toEdifact()).isEqualTo("DTM+815:202005282158:306'");
    }

    @Test
    void When_FromStringAndEdifactIsWinterUTC_Expect_InstantIsUTC() {
        assertThat(RecepMessageDateTime.fromString("DTM+815:202003282058:306'").getTimestamp()).isEqualTo(WINTER);
    }

    @Test
    void When_FromStringAndEdifactIsSummerBST_Expect_InstantIsUTC() {
        // the internal Instant representation (UTC) is one hour "behind" the EDIFACT timestamp which is UK local time / BST
        assertThat(RecepMessageDateTime.fromString("DTM+815:202005282158:306'").getTimestamp()).isEqualTo(SUMMER);
    }

    @Test
    void When_FromStringAndStringIsNotDTMSegment_Expect_ThrowsException() {
        assertThatThrownBy(() -> RecepMessageDateTime.fromString("DTM+123:456:789'")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}