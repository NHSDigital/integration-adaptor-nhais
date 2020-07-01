package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.repository.SchedulerTimestampRepository;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshMailBoxScheduler {

    private final SchedulerTimestampRepository schedulerTimestampRepository;
    private final TimestampService timestampService;
    private final ApplicationContext applicationContext;

    private static final String SCHEDULER_TYPE = "meshTimestamp";

    @SneakyThrows
    public boolean hasTimePassed(long seconds) {
        if (isSchedulerEnabled()) {
            return updateTimestamp(seconds);
        }
        return false;
    }

    private boolean isSchedulerEnabled() {
        return BooleanUtils.toBoolean(applicationContext.getEnvironment().getProperty("nhais.scheduler.enabled"));
    }

    private boolean updateTimestamp(long seconds) {
        return schedulerTimestampRepository.updateTimestamp(SCHEDULER_TYPE, timestampService.getCurrentTimestamp(), seconds);
    }
}
