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

    private final String exampleMessage = "UNB+UNOA:2+FHS1+GP05+020114:1619+00000064++RECEP+++EDIFACT TRANSFER'\n" +
        "UNH+00000028+RECEP:0:2:FH'\n" +
        "BGM++600+243:199305201355:306+64'\n" +
        "NHS+FHS:819:201+123456:814:202'\n" +
        "DTM+815:199305190600:306'\n" +
        "RFF+MIS:00000101 CP'\n" +
        "RFF+MIS:00000102 CA'\n" +
        "RFF+MIS:00000103 CI'\n" +
        "RFF+MIS:00000104 CP'\n" +
        "RFF+RIS:00000100 OK:4'\n" +
        "UNT+10+00000028'\n" +
        "UNZ+1+00000064'";

    @Test
    void testParsingInterchangeHeader() {
        var recepMessage = new RecepMessage(exampleMessage);
        InterchangeHeader interchangeHeader = recepMessage.getInterchangeHeader();

        assertThat(interchangeHeader.getSender()).isEqualTo("FHS1");
        assertThat(interchangeHeader.getRecipient()).isEqualTo("GP05");
        Instant expectedTime = ZonedDateTime
            .parse("020114:1619", DateTimeFormatter.ofPattern("yyMMdd:HHmm").withZone(ZoneId.of("Europe/London")))
            .toInstant();
        assertThat(interchangeHeader.getTranslationTime()).isEqualTo(expectedTime);
        assertThat(interchangeHeader.getSequenceNumber()).isEqualTo(64L);
    }

    @Test
    void testParsingReferenceInterchangeRecep() {
        var recepMessage = new RecepMessage(exampleMessage);
        var referenceInterchangeRecep = recepMessage.getReferenceInterchangeRecep();

        assertThat(referenceInterchangeRecep.getInterchangeSequenceNumber()).isEqualTo(100L);
        assertThat(referenceInterchangeRecep.getRecepCode()).isEqualTo(ReferenceInterchangeRecep.RecepCode.RECEIVED);
        assertThat(referenceInterchangeRecep.getMessageCount()).isEqualTo(4);
    }

    @Test
    void testParsingReferenceMessageRecep(SoftAssertions softly) {
        var recepMessage = new RecepMessage(exampleMessage);
        var referenceMessageReceps = recepMessage.getReferenceMessageReceps();

        var expectedReceps = List.of(
            Pair.of(101L, ReferenceMessageRecep.RecepCode.SUCCESS),
            Pair.of(102L, ReferenceMessageRecep.RecepCode.ERROR),
            Pair.of(103L, ReferenceMessageRecep.RecepCode.INCOMPLETE),
            Pair.of(104L, ReferenceMessageRecep.RecepCode.SUCCESS)
        );

        softly.assertThat(referenceMessageReceps.size()).isEqualTo(4);

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