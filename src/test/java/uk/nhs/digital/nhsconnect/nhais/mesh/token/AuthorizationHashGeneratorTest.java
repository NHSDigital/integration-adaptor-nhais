package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorizationHashGeneratorTest {

    @Mock
    private MeshConfig meshConfig;

    private final static String MAILBOX_ID = "mailbox_id";
    private final static String MAILBOX_PASSWORD = "mailbox_password";
    private final static String SHARED_KEY = "shared_key";

    private final static Instant FIXED_TIME_LOCAL = LocalDate.of(1991, 11, 6)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant();
    private final static String UUID = "73eefd69-811f-44d0-81f8-a54ff352a991";

    @BeforeEach
    void setUp() {
        when(meshConfig.getMailboxId()).thenReturn(MAILBOX_ID);
        when(meshConfig.getMailboxPassword()).thenReturn(MAILBOX_PASSWORD);
        when(meshConfig.getSharedKey()).thenReturn(SHARED_KEY);
    }

    @Test
    void testCorrectHashGenerated() {
        AuthorizationHashGenerator authorizationHashGenerator = new AuthorizationHashGenerator();

        Nonce nonce = new Nonce(UUID);

        String timestamp = new TokenTimestamp(FIXED_TIME_LOCAL).getValue();
        String hash = authorizationHashGenerator.computeHash(meshConfig, nonce, timestamp);
        assertThat(hash).isEqualTo("474c0634fd2267e41252bddfb40031d85e433599a8015c74546e95b05c2df569");
    }

}