package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import uk.nhs.digital.nhsconnect.nhais.scheduler.SchedulerTimestampRepository;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeshMailBoxSchedulerTest {

    @InjectMocks
    MeshMailBoxScheduler meshMailBoxScheduler;

    @Mock
    private SchedulerTimestampRepository schedulerTimestampRepository;

    @Mock
    private TimestampService timestampService;

    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void When_CollectionIsEmpty_Then_SingleDocumentIsCreatedAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(anyString(), isA(Instant.class), anyLong())).thenReturn(false);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());

        boolean hasTimePassed = meshMailBoxScheduler.hasTimePassed(5);

        assertThat(hasTimePassed).isFalse();
    }

    @Test
    public void When_DocumentExistsAndTimestampIsBeforeProvidedTime_Then_DocumentIsUpdateAndTheJobIsExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(anyString(), isA(Instant.class), anyLong())).thenReturn(true);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());

        boolean hasTimePassed = meshMailBoxScheduler.hasTimePassed(5);

        assertThat(hasTimePassed).isTrue();
    }

    @Test
    public void When_DocumentExistsAndTimestampIsAfterProvidedTime_Then_DocumentIsNotUpdateAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(anyString(), isA(Instant.class), anyLong())).thenReturn(false);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());

        boolean hasTimePassed = meshMailBoxScheduler.hasTimePassed(5);

        assertThat(hasTimePassed).isFalse();
    }

    @Test
    void When_SchedulerIsDisabled_Then_ReturnFalse() {
        Environment environment = mock(Environment.class);
        when(environment.getProperty("nhais.scheduler.enabled")).thenReturn("false");
        when(applicationContext.getEnvironment()).thenReturn(environment);

        assertThat(meshMailBoxScheduler.isEnabled()).isFalse();
    }

    @Test
    void When_SchedulerIsEnabled_Then_ReturnTrue() {
        Environment environment = mock(Environment.class);
        when(environment.getProperty("nhais.scheduler.enabled")).thenReturn("true");
        when(applicationContext.getEnvironment()).thenReturn(environment);

        assertThat(meshMailBoxScheduler.isEnabled()).isTrue();
    }
}
