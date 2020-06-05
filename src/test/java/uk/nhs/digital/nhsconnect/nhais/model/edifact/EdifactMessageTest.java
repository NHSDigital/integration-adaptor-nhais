package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactMessage;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class EdifactMessageTest {

    private final String exampleMessage = "UNB+UNOA:2+TES5+XX11+020114:1619+00000003'\n" +
        "UNH+00000004+FHSREG:0:1:FH:FHS001'\n" +
        "BGM+++507'\n" +
        "NAD+FHS+XX1:954'\n" +
        "DTM+137:199201141619:203'\n" +
        "RFF+950:G1'\n" +
        "S01+1'\n" +
        "RFF+TN:18'\n" +
        "NAD+GP+2750922,295:900'\n" +
        "NAD+RIC+RT:956'\n" +
        "QTY+951:6'\n" +
        "QTY+952:3'\n" +
        "HEA+ACD+A:ZZZ'\n" +
        "HEA+ATP+2:ZZZ'\n" +
        "HEA+BM+S:ZZZ'\n" +
        "HEA+DM+Y:ZZZ'\n" +
        "DTM+956:19920114:102'\n" +
        "LOC+950+GLASGOW'\n" +
        "FTX+RGI+++BABY AT THE REYNOLDS-THORPE CENTRE'\n" +
        "S02+2'\n" +
        "PNA+PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'\n" +
        "DTM+329:19911209:102'\n" +
        "PDI+2'\n" +
        "NAD+PAT++??:26 FARMSIDE CLOSE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7ET'\n" +
        "UNT+24+00000004'\n" +
        "UNZ+1+00000003'";

    @Test
    void testParsingInterchangeHeader() {
        EdifactMessage edifactMessage = new EdifactMessage(exampleMessage);
        InterchangeHeader interchangeHeader = edifactMessage.getInterchangeHeader();

        assertThat(interchangeHeader.getSender()).isEqualTo("TES5");
        assertThat(interchangeHeader.getRecipient()).isEqualTo("XX11");

        Instant expectedTime = ZonedDateTime.parse("020114:1619", DateTimeFormatter.ofPattern("yyMMdd:HHmm").withZone(TimestampService.UKZone)).toInstant();
        assertThat(interchangeHeader.getTranslationTime()).isEqualTo(expectedTime);
        assertThat(interchangeHeader.getSequenceNumber()).isEqualTo(3L);
    }

    @Test
    void testParsingMessageHeader() {
        EdifactMessage edifactMessage = new EdifactMessage(exampleMessage);

        assertThat(edifactMessage.getMessageHeader().getValue()).isEqualTo("00000004+FHSREG:0:1:FH:FHS001");
        assertThat(edifactMessage.getMessageHeader().getSequenceNumber()).isEqualTo(4L);
    }

    @Test
    void testParsingReferenceTransactionNumber() {
        EdifactMessage edifactMessage = new EdifactMessage(exampleMessage);
        ReferenceTransactionNumber referenceTransactionNumber = edifactMessage.getReferenceTransactionNumber();

        assertThat(referenceTransactionNumber.getValue()).isEqualTo("TN:18");
        assertThat(referenceTransactionNumber.getTransactionNumber()).isEqualTo(18L);
    }

    @Test
    void testParsingReferenceTransactionType() {
        EdifactMessage edifactMessage = new EdifactMessage(exampleMessage);
        ReferenceTransactionType referenceTransactionType = edifactMessage.getReferenceTransactionType();

        assertThat(referenceTransactionType.getValue()).isEqualTo("950:G1");
        assertThat(referenceTransactionType.getTransactionType().getCode()).isEqualTo("G1");
    }

    @Test
    void testHealthAuthorityNameAndAddress(SoftAssertions softly) {
        EdifactMessage edifactMessage = new EdifactMessage(exampleMessage);
        HealthAuthorityNameAndAddress healthAuthorityNameAndAddress = edifactMessage.getHealthAuthorityNameAndAddress();

        softly.assertThat(healthAuthorityNameAndAddress.getValue()).isEqualTo("FHS+XX1:954");
        softly.assertThat(healthAuthorityNameAndAddress.getIdentifier()).isEqualTo("XX1");
        softly.assertThat(healthAuthorityNameAndAddress.getCode()).isEqualTo("954");
    }

    @Test
    void testGpNameAndAddress(SoftAssertions softly) {
        EdifactMessage edifactMessage = new EdifactMessage(exampleMessage);
        GpNameAndAddress gpNameAndAddress = edifactMessage.getGpNameAndAddress();

        softly.assertThat(gpNameAndAddress.getValue()).isEqualTo("GP+2750922,295:900");
        softly.assertThat(gpNameAndAddress.getIdentifier()).isEqualTo("2750922,295");
        softly.assertThat(gpNameAndAddress.getCode()).isEqualTo("900");
    }

    @Test
    void testTranslationTimeDateTimePeriod(SoftAssertions softly) {
        EdifactMessage edifactMessage = new EdifactMessage(exampleMessage);
        DateTimePeriod dateTimePeriod = edifactMessage.getTranslationDateTime();

        softly.assertThat(dateTimePeriod.getValue()).isEqualTo("137:199201141619:203");
        Instant expectedTimestamp = ZonedDateTime.parse("199201141619", DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP.getDateTimeFormat()).toInstant();
        softly.assertThat(dateTimePeriod.getTimestamp()).isEqualTo(expectedTimestamp);
        softly.assertThat(dateTimePeriod.getTypeAndFormat()).isEqualTo(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);
    }
}