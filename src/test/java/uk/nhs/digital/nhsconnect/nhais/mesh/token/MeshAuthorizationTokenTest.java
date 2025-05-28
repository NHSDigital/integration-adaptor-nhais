package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeshAuthorizationTokenTest {

    private static final String AUTHORIZATION_HASH = "474c0634fd2267e41252bddfb40031d85e433599a8015c74546e95b05c2df569";
    private static final String MAILBOX_ID = "mailbox_id";
    private static final int EDIFACT_MAILBOX_INDEX = 0;
    private static final int EDIFACT_NONCE_VALUE_INDEX = 1;
    private static final int EDIFACT_NONCE_COUNT_INDEX = 2;
    private static final int EDIFACT_TIMESTAMP_INDEX = 3;
    private static final int EDIFACT_AUTHORIZATION_HASH_INDEX = 4;

    private static final Instant FIXED_TIME_LOCAL = ZonedDateTime
        .of(LocalDateTime.parse("1991-11-06T12:30:00"), TimestampService.UK_ZONE)
        .toInstant();
    private static final String UUID = "73eefd69-811f-44d0-81f8-a54ff352a991";

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
        MeshAuthorizationToken meshToken = new MeshAuthorizationToken(
            meshConfig,
            FIXED_TIME_LOCAL,
            new Nonce(UUID),
            authorizationHashGenerator
        );

        SoftAssertions.assertSoftly(softly -> {
            String[] values = meshToken.getValue().split(":");
            softly.assertThat(values[EDIFACT_MAILBOX_INDEX])
                .isEqualTo("NHSMESH " + MAILBOX_ID);
            softly.assertThat(values[EDIFACT_NONCE_VALUE_INDEX])
                .isEqualTo(UUID);
            softly.assertThat(values[EDIFACT_NONCE_COUNT_INDEX])
                .isEqualTo("1");
            softly.assertThat(values[EDIFACT_TIMESTAMP_INDEX])
                .isEqualTo(new TokenTimestamp(FIXED_TIME_LOCAL).getValue());
            softly.assertThat(values[EDIFACT_AUTHORIZATION_HASH_INDEX])
                .isEqualTo(AUTHORIZATION_HASH);
            softly.assertThat(meshToken.getValue())
                .isEqualTo(
                    "NHSMESH mailbox_id:73eefd69-811f-44d0-81f8-a54ff352a991:1"
                        + ":199111061230:474c0634fd2267e41252bddfb40031d85e433599a8015c74546e95b05c2df569"
                );
        });
    }

}