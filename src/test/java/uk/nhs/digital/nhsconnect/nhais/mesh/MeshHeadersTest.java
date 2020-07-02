package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MeshHeadersTest {

    private final MeshConfig meshConfig = new FakeMeshConfig();
    private final MeshHeaders meshHeaders = new MeshHeaders(meshConfig);

    @Test
    void createSendHeaders() {
        String meshRecipient = "meshRecipient";
        List<String> headerNames = Arrays.stream(meshHeaders.createSendHeaders(meshRecipient, WorkflowId.REGISTRATION))
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

        BasicHeader mexToHeader = Arrays.stream(meshHeaders.createSendHeaders(meshRecipient, WorkflowId.REGISTRATION))
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