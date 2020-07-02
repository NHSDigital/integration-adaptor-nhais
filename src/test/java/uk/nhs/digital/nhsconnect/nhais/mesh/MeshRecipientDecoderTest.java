package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MeshRecipientDecoderTest {

    private final MeshRecipientDecoder meshRecipientDecoder = new MeshRecipientDecoder(new EdifactParser(), "XX11=A91561OT001\nabc=cde");

    private final String edifactString = "UNB+UNOA:2+TES5+XX11+200610:1438+00000001'\n" +
        "UNH+00000001+FHSREG:0:1:FH:FHS001'\n" +
        "BGM+++507'\n" +
        "NAD+FHS+XX1:954'\n" +
        "DTM+137:202006101438:203'\n" +
        "RFF+950:G1'\n" +
        "S01+1'\n" +
        "RFF+TN:1'\n" +
        "NAD+GP+4826940,281:900'\n" +
        "HEA+ACD+A:ZZZ'\n" +
        "HEA+ATP+1:ZZZ'\n" +
        "DTM+956:19920113:102'\n" +
        "S02+2'\n" +
        "PNA+PAT+N/10/10:OPI+++SU:STEVENS'\n" +
        "DTM+329:19911106:102'\n" +
        "PDI+1'\n" +
        "NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT'\n" +
        "UNT+17+00000001'\n" +
        "UNZ+1+00000001'";

    private final String edifactStringWithUnknownRecipient = "UNB+UNOA:2+TES5+9999+200610:1438+00000001'\n" +
        "UNH+00000001+FHSREG:0:1:FH:FHS001'\n" +
        "BGM+++507'\n" +
        "NAD+FHS+XX1:954'\n" +
        "DTM+137:202006101438:203'\n" +
        "RFF+950:G1'\n" +
        "S01+1'\n" +
        "RFF+TN:1'\n" +
        "NAD+GP+4826940,281:900'\n" +
        "HEA+ACD+A:ZZZ'\n" +
        "HEA+ATP+1:ZZZ'\n" +
        "DTM+956:19920113:102'\n" +
        "S02+2'\n" +
        "PNA+PAT+N/10/10:OPI+++SU:STEVENS'\n" +
        "DTM+329:19911106:102'\n" +
        "PDI+1'\n" +
        "NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT'\n" +
        "UNT+17+00000001'\n" +
        "UNZ+1+00000001'";

    @Test
    void When_RecipientCypherAvailable_Then_MapToCorrectValue() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setContent(edifactString);
        assertThat(meshRecipientDecoder.getRecipient(meshMessage)).isEqualTo("A91561OT001");
        assertThat(meshRecipientDecoder.getRecipient(meshMessage)).isEqualTo("A91561OT001");
    }

    @Test
    void When_RecipientCypherIsUnavailable_Then_ThrowException() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setContent(edifactStringWithUnknownRecipient);
        assertThatThrownBy(() -> meshRecipientDecoder.getRecipient(meshMessage))
            .isExactlyInstanceOf(MeshRecipientUnknownException.class)
            .hasMessage("Couldn't decode recipient: 9999");
    }
}