package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import java.time.Instant;

import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

/**
 * MESH authorization token
 * One time use only - each MESH API call should use new token
 */
public class MeshAuthorizationToken {

    private final static String MESSAGE_TYPE = "NHSMESH ";

    private final String data;
    private final String hash;

    public MeshAuthorizationToken(MeshConfig meshConfig, Instant timestamp, Nonce nonce, AuthorizationHashGenerator authorizationHashGenerator) {
        String prefix = MESSAGE_TYPE + meshConfig.getMailboxId();
        String currentTimeFormatted = new TokenTimestamp(timestamp).getValue();
        this.data = String.join(":", prefix, nonce.value, nonce.count, currentTimeFormatted);
        this.hash = authorizationHashGenerator.computeHash(meshConfig, nonce, currentTimeFormatted);
    }

    public MeshAuthorizationToken(MeshConfig meshConfig) {
        this(meshConfig, new TimestampService().getCurrentTimestamp(), new Nonce(), new AuthorizationHashGenerator());
    }

    public String getValue(){
        return String.join(":", data, hash);
    }

}
