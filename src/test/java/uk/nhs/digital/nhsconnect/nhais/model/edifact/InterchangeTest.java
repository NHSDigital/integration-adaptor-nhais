package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.MissingSegmentException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterchangeTest {

    private final String correctMessage = "UNB+UNOA:2+TES5+XX11+020114:1619+00000003'\n" +
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

    private final String messageWithEmptySegments = "S01+1'\n" +
        "S02+2'\n" +
        "UNT+24+00000004'\n" +
        "UNZ+1+00000003'";

    @Test
    void testValidateOk() {
        Interchange interchange = new Interchange(new EdifactMessage(correctMessage));
        assertThatCode(interchange::validate).doesNotThrowAnyException();
    }

    @Test
    void testEmptyMessageThrowsSegmentGroupMissingException() {
        Interchange interchange = new Interchange(new EdifactMessage("example invalid edifact message"));
        assertThatThrownBy(interchange::getGpNameAndAddress)
            .isExactlyInstanceOf(MissingSegmentException.class);
    }

    @Test
    void testInterchangeHeaderMissing() {
        Interchange interchange = new Interchange(new EdifactMessage(messageWithEmptySegments));
        assertThatThrownBy(interchange::getInterchangeHeader)
            .isExactlyInstanceOf(MissingSegmentException.class)
            .hasMessageContaining("UNB");
    }

    @Test
    void testMessageHeaderMissing() {
        Interchange interchange = new Interchange(new EdifactMessage(messageWithEmptySegments));
        assertThatThrownBy(interchange::getMessageHeader)
            .isExactlyInstanceOf(MissingSegmentException.class)
            .hasMessageContaining("UNH");
    }

    @Test
    void testReferenceTransactionNumberMissing() {
        Interchange interchange = new Interchange(new EdifactMessage(messageWithEmptySegments));
        assertThatThrownBy(interchange::getReferenceTransactionNumber)
            .isExactlyInstanceOf(MissingSegmentException.class)
            .hasMessageContaining("RFF+TN");
    }

    @Test
    void testTranslationDateTimeMissing() {
        Interchange interchange = new Interchange(new EdifactMessage(messageWithEmptySegments));
        assertThatThrownBy(interchange::getTranslationDateTime)
            .isExactlyInstanceOf(MissingSegmentException.class)
            .hasMessageContaining("DTM");
    }

    @Test
    void testReferenceTransactionTypeMissing() {
        Interchange interchange = new Interchange(new EdifactMessage(messageWithEmptySegments));
        assertThatThrownBy(interchange::getReferenceTransactionType)
            .isExactlyInstanceOf(MissingSegmentException.class)
            .hasMessageContaining("RFF+950");
    }

    @Test
    void testHealthAuthorityNameAndAddressMissing() {
        Interchange interchange = new Interchange(new EdifactMessage(messageWithEmptySegments));
        assertThatThrownBy(interchange::getHealthAuthorityNameAndAddress)
            .isExactlyInstanceOf(MissingSegmentException.class)
            .hasMessageContaining("NAD+FHS");
    }

    @Test
    void testGpNameAndAddressMissing() {
        Interchange interchange = new Interchange(new EdifactMessage(messageWithEmptySegments));
        assertThatThrownBy(interchange::getGpNameAndAddress)
            .isExactlyInstanceOf(MissingSegmentException.class)
            .hasMessageContaining("NAD+GP");
    }
}