package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZonedDateTime;

import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeshAuthorizationTokenTest {

    private final static String AUTHORIZATION_HASH = "474c0634fd2267e41252bddfb40031d85e433599a8015c74546e95b05c2df569";
    private final static String MAILBOX_ID = "mailbox_id";

    private final static Instant FIXED_TIME_LOCAL = ZonedDateTime.of(1991,11,6,12,30,0,0, TimestampService.UKZone)
        .toInstant();
    private final static String UUID = "73eefd69-811f-44d0-81f8-a54ff352a991";

    @Mock
    private MeshConfig meshConfig;
    @Mock
    private AuthorizationHashGenerator authorizationHashGenerator;

    @BeforeEach
    void setUp() {
        when(meshConfig.getMailboxId()).thenReturn(MAILBOX_ID);
        when(authorizationHashGenerator.computeHash(any(), any(), any())).thenReturn(AUTHORIZATION_HASH);
    }

    @Test
    void testTokenUsesCorrectFormat() {
        MeshAuthorizationToken meshToken = new MeshAuthorizationToken(meshConfig, FIXED_TIME_LOCAL, new Nonce(UUID), authorizationHashGenerator);
        SoftAssertions.assertSoftly(softly -> {
            String[] values = meshToken.getValue().split(":");
            softly.assertThat(values[0]).isEqualTo("NHSMESH " + MAILBOX_ID);
            softly.assertThat(values[1]).isEqualTo(UUID);
            softly.assertThat(values[2]).isEqualTo("1");
            softly.assertThat(values[3]).isEqualTo(new TokenTimestamp(FIXED_TIME_LOCAL).getValue());
            softly.assertThat(values[4]).isEqualTo(AUTHORIZATION_HASH);
            softly.assertThat(meshToken.getValue())
                .isEqualTo("NHSMESH mailbox_id:73eefd69-811f-44d0-81f8-a54ff352a991:1:199111061230:474c0634fd2267e41252bddfb40031d85e433599a8015c74546e95b05c2df569");
        });
    }

}