package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampUtils;

import java.time.ZonedDateTime;

@Component
public class TimestampService {

    public ZonedDateTime getCurrentTimestamp() {
        return TimestampUtils.getCurrentDateTimeAsUTC();
    }

}
