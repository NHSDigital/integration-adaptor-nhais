package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RecepTimestampTest {

    private final Instant WINTER = ZonedDateTime
        .of(2020, 3, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final Instant SUMMER = ZonedDateTime
        .of(2020, 5, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();

    @Test
    public void When_toEdifact_And_instantInWinter_Then_edifactIsUTC() throws EdifactValidationException {
        assertThat(new RecepTimestamp(WINTER).toEdifact()).isEqualTo("DTM+815:202003282058:306'");
    }

    @Test
    public void When_toEdifact_And_instantInSummer_Then_edifactIsBST() throws EdifactValidationException {
        // the translated times are UK local time / BST and one hour "ahead" of UTC
        assertThat(new RecepTimestamp(SUMMER).toEdifact()).isEqualTo("DTM+815:202005282158:306'");
    }

    @Test
    void When_fromString_And_edifactIsWinterUTC_Then_instantIsUTC() {
        assertThat(RecepTimestamp.fromString("DTM+815:202003282058:306'").getTimestamp()).isEqualTo(WINTER);
    }

    @Test
    void When_fromString_And_edifactIsSummerBST_Then_instantIsUTC() {
        // the internal Instant representation (UTC) is one hour "behind" the EDIFACT timestamp which is UK local time / BST
        assertThat(RecepTimestamp.fromString("DTM+815:202005282158:306'").getTimestamp()).isEqualTo(SUMMER);
    }

    @Test
    void When_fromString_And_stringIsNotDTMSegment_Then_throwsException() {
        assertThatThrownBy(() -> RecepTimestamp.fromString("DTM+123:456:789'")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}