package uk.nhs.digital.nhsconnect.nhais.mesh;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.mesh.token.MeshAuthorizationToken;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHeaders {

    private final MeshConfig meshConfig;

    private static final List<BasicHeader> OS_HEADERS = List.of(
        new BasicHeader("Mex-ClientVersion", "1.0"),
        new BasicHeader("Mex-OSVersion", "1.0"),
        new BasicHeader("Mex-OSName", "Unix"));

    public Header[] createSendHeaders(String recipient) {
        List<BasicHeader> sendHeaders = List.of(
            new BasicHeader("Mex-From", meshConfig.getMailboxId()),
            new BasicHeader("Mex-To", recipient),
            new BasicHeader("Mex-WorkflowID", "workflow1"),
            new BasicHeader("Mex-FileName", "edifact.dat"),
            new BasicHeader("Mex-MessageType", "DATA"));
        return Stream.concat(Arrays.stream(createMinimalHeaders()), sendHeaders.stream())
            .toArray(Header[]::new);
    }

    public Header[] createMinimalHeaders() {
        List<BasicHeader> authorization = List.of(new BasicHeader("Authorization", new MeshAuthorizationToken(meshConfig).getValue()));
        return Stream.concat(OS_HEADERS.stream(), authorization.stream())
            .toArray(Header[]::new);
    }

}
