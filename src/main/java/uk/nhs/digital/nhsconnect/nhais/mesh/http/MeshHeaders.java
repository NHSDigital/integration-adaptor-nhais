package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.token.MeshAuthorizationToken;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHeaders {

    private static final List<BasicHeader> OS_HEADERS = List.of(
        new BasicHeader("Mex-ClientVersion", "1.0"),
        new BasicHeader("Mex-OSVersion", Optional.ofNullable(SystemUtils.OS_VERSION).orElse("1.0")),
        new BasicHeader("Mex-OSName", Optional.ofNullable(SystemUtils.OS_NAME).orElse("Unix"))
    );
    private final MeshConfig meshConfig;

    public Header[] createSendHeaders(String recipient, WorkflowId workflowId) {
        List<BasicHeader> sendHeaders = List.of(
            new BasicHeader("Mex-From", meshConfig.getMailboxId()),
            new BasicHeader("Mex-To", recipient),
            new BasicHeader("Mex-WorkflowID", workflowId.getWorkflowId()),
            new BasicHeader("Mex-FileName", "edifact.dat"),
            new BasicHeader("Mex-MessageType", "DATA"),
            new BasicHeader("Mex-Content-Compressed", "N"),
            new BasicHeader("Content-Type", "application/octet"));
        return Stream.concat(Arrays.stream(createMinimalHeaders()), sendHeaders.stream())
            .toArray(Header[]::new);
    }

    public Header[] createMinimalHeaders() {
        List<BasicHeader> authorization = List.of(new BasicHeader("Authorization", new MeshAuthorizationToken(meshConfig).getValue()));
        return Stream.concat(OS_HEADERS.stream(), authorization.stream())
            .toArray(Header[]::new);
    }

    public Header[] createAuthenticateHeaders() {
        List<BasicHeader> authHeaders = List.of(
            new BasicHeader("Mex-JavaVersion", Runtime.version().toString()),
            new BasicHeader("Mex-OSArchitecture", Optional.ofNullable(SystemUtils.OS_ARCH).orElse("Unix")));
        return Stream.concat(Arrays.stream(createMinimalHeaders()), authHeaders.stream())
            .toArray(Header[]::new);
    }

}
