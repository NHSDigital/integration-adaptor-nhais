package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class RecepMessageTest {

    private static final long FIRST_SUCCESS_KEY = 101L;
    private static final long ERROR_KEY = 102L;
    private static final long INCOMPLETE_KEY = 103L;
    private static final long SECOND_SUCCESS_KEY = 104L;
    private final String exampleMessage = """
        UNB+UNOA:2+FHS1+GP05+020114:1619+00000064++RECEP+++EDIFACT TRANSFER'
        UNH+00000028+RECEP:0:2:FH'
        BGM++600+243:199305201355:306+64'
        NHS+FHS:819:201+123456:814:202'
        DTM+815:199305190600:306'
        RFF+MIS:00000101 CP'
        RFF+MIS:00000102 CA'
        RFF+MIS:00000103 CI'
        RFF+MIS:00000104 CP'
        RFF+RIS:00000100 OK:4'
        UNT+10+00000028'
        UNZ+1+00000064'""";

    @Test
    void testParsingInterchangeHeader() {
        final long expectedSequenceNumber = 64L;
        var recepMessage = new RecepMessage(exampleMessage);
        InterchangeHeader interchangeHeader = recepMessage.getInterchangeHeader();

        assertThat(interchangeHeader.getSender()).isEqualTo("FHS1");
        assertThat(interchangeHeader.getRecipient()).isEqualTo("GP05");
        Instant expectedTime = ZonedDateTime
            .parse("020114:1619", DateTimeFormatter.ofPattern("yyMMdd:HHmm").withZone(ZoneId.of("Europe/London")))
            .toInstant();
        assertThat(interchangeHeader.getTranslationTime()).isEqualTo(expectedTime);
        assertThat(interchangeHeader.getSequenceNumber()).isEqualTo(expectedSequenceNumber);
    }

    @Test
    void testParsingReferenceInterchangeRecep() {
        final long expectedInterchangeSequenceNumber = 100L;
        final int expectedMessageCount = 4;
        var recepMessage = new RecepMessage(exampleMessage);
        var referenceInterchangeRecep = recepMessage.getReferenceInterchangeRecep();

        assertThat(referenceInterchangeRecep.getInterchangeSequenceNumber()).isEqualTo(expectedInterchangeSequenceNumber);
        assertThat(referenceInterchangeRecep.getRecepCode()).isEqualTo(ReferenceInterchangeRecep.RecepCode.RECEIVED);
        assertThat(referenceInterchangeRecep.getMessageCount()).isEqualTo(expectedMessageCount);
    }

    @Test
    void testParsingReferenceMessageRecep(SoftAssertions softly) {
        final int expectedReferenceMessageCount = 4;
        var recepMessage = new RecepMessage(exampleMessage);
        var referenceMessageReceps = recepMessage.getReferenceMessageReceps();

        var expectedReceps = List.of(
            Pair.of(FIRST_SUCCESS_KEY, ReferenceMessageRecep.RecepCode.SUCCESS),
            Pair.of(ERROR_KEY, ReferenceMessageRecep.RecepCode.ERROR),
            Pair.of(INCOMPLETE_KEY, ReferenceMessageRecep.RecepCode.INCOMPLETE),
            Pair.of(SECOND_SUCCESS_KEY, ReferenceMessageRecep.RecepCode.SUCCESS)
        );

        softly.assertThat(referenceMessageReceps.size()).isEqualTo(expectedReferenceMessageCount);

        for (int i = 0; i < referenceMessageReceps.size(); i++) {
            softly
                .assertThat(referenceMessageReceps.get(i).getMessageSequenceNumber())
                .isEqualTo(expectedReceps.get(i).getLeft());
            softly
                .assertThat(referenceMessageReceps.get(i).getRecepCode())
                .isEqualTo(expectedReceps.get(i).getRight());
        }
    }
}