package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MeshHeadersTest {

    private final MeshConfig meshConfig = new FakeMeshConfig();
    private final MeshHeaders meshHeaders = new MeshHeaders(meshConfig);

    @Test
    void createSendHeaders() {
        String meshRecipient = "some_recipient";

        Header[] headers = meshHeaders.createSendHeaders(meshRecipient, WorkflowId.REGISTRATION);

        List<String> headerNames = Arrays.stream(headers)
            .map(BasicHeader.class::cast)
            .map(BasicHeader::getName)
            .collect(Collectors.toList());
        assertThat(headerNames).containsExactlyInAnyOrder(
            "Mex-ClientVersion",
            "Mex-OSVersion",
            "Mex-OSName",
            "Authorization",
            "Mex-From",
            "Mex-To",
            "Mex-WorkflowID",
            "Mex-FileName",
            "Mex-MessageType");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getHeaderValue(headers, "Mex-ClientVersion")).isEqualTo("1.0");
            softly.assertThat(getHeaderValue(headers, "Mex-OSVersion")).isEqualTo("1.0");
            softly.assertThat(getHeaderValue(headers, "Mex-OSName")).isEqualTo("Unix");
            softly.assertThat(getHeaderValue(headers, "Authorization")).startsWith("NHSMESH mailboxId:");
            softly.assertThat(getHeaderValue(headers, "Mex-From")).isEqualTo("mailboxId");
            softly.assertThat(getHeaderValue(headers, "Mex-To")).isEqualTo(meshRecipient);
            softly.assertThat(getHeaderValue(headers, "Mex-WorkflowID")).isEqualTo("NHAIS_REG");
            softly.assertThat(getHeaderValue(headers, "Mex-FileName")).isEqualTo("edifact.dat");
            softly.assertThat(getHeaderValue(headers, "Mex-MessageType")).isEqualTo("DATA");
        });
    }

    @Test
    void createMinimalHeaders() {
        Header[] headers = meshHeaders.createMinimalHeaders();

        List<String> headerNames = Arrays.stream(headers)
            .map(BasicHeader.class::cast)
            .map(BasicHeader::getName)
            .collect(Collectors.toList());
        assertThat(headerNames).containsExactlyInAnyOrder(
            "Mex-ClientVersion",
            "Mex-OSVersion",
            "Mex-OSName",
            "Authorization");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getHeaderValue(headers, "Mex-ClientVersion")).isEqualTo("1.0");
            softly.assertThat(getHeaderValue(headers, "Mex-OSVersion")).isEqualTo("1.0");
            softly.assertThat(getHeaderValue(headers, "Mex-OSName")).isEqualTo("Unix");
            softly.assertThat(getHeaderValue(headers, "Authorization")).startsWith("NHSMESH mailboxId:");
        });
    }

    private String getHeaderValue(Header[] headers, String header) {
        return Arrays.stream(headers)
            .map(BasicHeader.class::cast)
            .filter(h -> header.equals(h.getName()))
            .findFirst()
            .map(BasicHeader::getValue)
            .orElseThrow();
    }
}