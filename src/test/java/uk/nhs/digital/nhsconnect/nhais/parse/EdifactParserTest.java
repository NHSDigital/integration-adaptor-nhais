package uk.nhs.digital.nhsconnect.nhais.parse;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EdifactParserTest {

    private final String exampleMessage = "UNB+UNOA:2+TES5+XX11+920114:1619+00000003'\n" +
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
    void testParseIntoInterchange() {
        Interchange interchange = new EdifactParser().parse(exampleMessage);
        assertThat(interchange.getInterchangeHeader().getValue()).isEqualTo("UNOA:2+TES5+XX11+920114:1619+00000003");
        assertThat(interchange.getMessageHeader().getValue()).isEqualTo("00000004+FHSREG:0:1:FH:FHS001");
        assertThat(interchange.getReferenceTransactionNumber().getValue()).isEqualTo("TN:18");
        assertThat(interchange.getReferenceTransactionType().getValue()).isEqualTo("950:G1");
        assertThat(interchange.getHealthAuthorityNameAndAddress().getValue()).isEqualTo("FHS+XX1:954");
        assertThat(interchange.getGpNameAndAddress().getValue()).isEqualTo("GP+2750922,295:900");
        assertThat(interchange.getTranslationDateTime().getValue()).isEqualTo("137:199201141619:203");
    }

    @Test
    void testValidateInterchange() {
        String messageWithEmptySegments = "S01+1'\n" +
            "S02+2'\n" +
            "UNT+24+00000004'\n" +
            "UNZ+1+00000003'";

        String messageWithEmptySender = "UNB+UNOA:2++XX11+920114:1619+00000003'\n" +
            "BGM+++507'\n" +
            "S01+1'\n" +
            "S02+2'\n" +
            "UNT+24+00000004'\n" +
            "UNZ+1+00000003'";

        assertThatThrownBy(() -> new EdifactParser().parse(messageWithEmptySegments))
            .isExactlyInstanceOf(ToEdifactParsingException.class)
            .hasMessageContaining("missing segment UNB")
            .hasMessageContaining("missing segment UNH")
            .hasMessageContaining("missing segment RFF+TN")
            .hasMessageContaining("missing segment RFF+950")
            .hasMessageContaining("missing segment DTM")
            .hasMessageContaining("missing segment NAD+FHS")
            .hasMessageContaining("missing segment NAD+GP");

        assertThatThrownBy(() -> new EdifactParser().parse(messageWithEmptySender))
            .isExactlyInstanceOf(ToEdifactParsingException.class)
            .hasMessageContaining("UNB: Attribute sender is required")
            .hasMessageContaining("missing segment UNH")
            .hasMessageContaining("missing segment RFF+TN")
            .hasMessageContaining("missing segment RFF+950")
            .hasMessageContaining("missing segment DTM")
            .hasMessageContaining("missing segment NAD+FHS")
            .hasMessageContaining("missing segment NAD+GP");
    }
}