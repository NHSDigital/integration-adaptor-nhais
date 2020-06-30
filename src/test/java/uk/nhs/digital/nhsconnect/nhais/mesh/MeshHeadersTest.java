package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;

class MeshHeadersTest {

    private final MeshConfig meshConfig = new FakeMeshConfig();
    private final MeshHeaders meshHeaders = new MeshHeaders(meshConfig);

    @Test
    void createSendHeaders() {
        String meshRecipient = "meshRecipient";
        List<String> headerNames = Arrays.stream(meshHeaders.createSendHeaders(meshRecipient))
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

        BasicHeader mexToHeader = Arrays.stream(meshHeaders.createSendHeaders(meshRecipient))
            .map(BasicHeader.class::cast)
            .filter(header -> "Mex-To".equals(header.getName()))
            .findFirst()
            .orElse(null);
        assertThat(mexToHeader.getValue()).isEqualTo(meshRecipient);
    }

    @Test
    void createMinimalHeaders() {
        List<String> headerNames = Arrays.stream(meshHeaders.createMinimalHeaders())
            .map(BasicHeader.class::cast)
            .map(BasicHeader::getName)
            .collect(Collectors.toList());
        assertThat(headerNames).containsExactlyInAnyOrder(
            "Mex-ClientVersion",
            "Mex-OSVersion",
            "Mex-OSName",
            "Authorization");
    }
}