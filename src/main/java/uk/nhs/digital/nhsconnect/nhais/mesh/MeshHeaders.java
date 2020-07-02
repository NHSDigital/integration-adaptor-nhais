package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.token.MeshAuthorizationToken;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshHeaders {

    private static final List<BasicHeader> OS_HEADERS = List.of(
        new BasicHeader("Mex-ClientVersion", "1.0"),
        new BasicHeader("Mex-OSVersion", "1.0"),
        new BasicHeader("Mex-OSName", "Unix"));
    private final MeshConfig meshConfig;

    public Header[] createSendHeaders(String recipient) {
        List<BasicHeader> sendHeaders = List.of(
            new BasicHeader("Mex-From", meshConfig.getMailboxId()),
            new BasicHeader("Mex-To", recipient),
            //TODO NIAD-122 distinguish REG and RECEP messages
            new BasicHeader("Mex-WorkflowID", "NHAIS_REG"),
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
