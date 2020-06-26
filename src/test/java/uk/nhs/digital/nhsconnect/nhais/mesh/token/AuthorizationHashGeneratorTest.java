package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZonedDateTime;

import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

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

    private final static Instant FIXED_TIME_LOCAL = ZonedDateTime.of(1991,11,6,12,30,0,0, TimestampService.UKZone)
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
        assertThat(hash).isEqualTo("e80adf34b261ba9a377ac4776e1354f2ce814c5f6ecec71b2ce540b94836c530");
    }

}