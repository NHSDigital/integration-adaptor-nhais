package uk.nhs.digital.nhsconnect.nhais;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.container.ActiveMqContainer;
import uk.nhs.digital.nhsconnect.nhais.container.FakeMeshContainer;
import uk.nhs.digital.nhsconnect.nhais.container.MongoDbContainer;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.SequenceDao;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.springframework.jms.support.destination.JmsDestinationAccessor.RECEIVE_TIMEOUT_NO_WAIT;
import static uk.nhs.digital.nhsconnect.nhais.jms.MeshServiceBaseTest.DLQ_PREFIX;

@Slf4j
public class SchedulerTestExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);
    }


}
