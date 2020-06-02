package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.container.MongoDbInitializer;
import uk.nhs.digital.nhsconnect.nhais.model.sequence.OutboundSequenceId;
import uk.nhs.digital.nhsconnect.nhais.repository.SequenceDao;
import uk.nhs.digital.nhsconnect.nhais.service.SequenceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = MongoDbInitializer.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@DirtiesContext
public class SequenceServiceIntegrationTest {
    private final static String SENDER_1 = "test-sender-1";
    private final static String SENDER_2 = "test-sender-2";
    private final static String RECIPIENT_1 = "test-recipient-1";
    private final static String RECIPIENT_2 = "test-recipient-2";
    private final static String TRANSACTION_KEY = "transaction_id";
    private final static String INTERCHANGE_KEY_1 = String.format("SIS-%s-%s", SENDER_1, RECIPIENT_1);
    private final static String INTERCHANGE_KEY_2 = String.format("SIS-%s-%s", SENDER_2, RECIPIENT_2);
    private final static String INTERCHANGE_MESSAGE_KEY_1 = String.format("SMS-%s-%s", SENDER_1, RECIPIENT_1);
    private final static String INTERCHANGE_MESSAGE_KEY_2 = String.format("SMS-%s-%s", SENDER_2, RECIPIENT_2);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SequenceService sequenceService;
    @Autowired
    private SequenceDao sequenceDao;

    @Test
    public void When_GenerateTransactionId_Then_IncreasedByOne() {
        resetCounter(TRANSACTION_KEY);

        assertThat(sequenceService.generateTransactionId()).isEqualTo(1L);
        assertThat(sequenceService.generateTransactionId()).isEqualTo(2L);
        assertThat(sequenceService.generateTransactionId()).isEqualTo(3L);
    }

    @Test
    public void When_GenerateInterchangeId_Then_IncreasedByOne() {
        resetCounter(INTERCHANGE_KEY_1);

        assertThat(sequenceService.generateInterchangeId(SENDER_1, RECIPIENT_1)).isEqualTo(1L);
        assertThat(sequenceService.generateInterchangeId(SENDER_1, RECIPIENT_1)).isEqualTo(2L);
        assertThat(sequenceService.generateInterchangeId(SENDER_1, RECIPIENT_1)).isEqualTo(3L);
    }

    @Test
    public void When_GenerateMessageId_Then_IncreasedByOne() {
        resetCounter(INTERCHANGE_MESSAGE_KEY_1);

        assertThat(sequenceService.generateMessageId(SENDER_1, RECIPIENT_1)).isEqualTo(1L);
        assertThat(sequenceService.generateMessageId(SENDER_1, RECIPIENT_1)).isEqualTo(2L);
        assertThat(sequenceService.generateMessageId(SENDER_1, RECIPIENT_1)).isEqualTo(3L);
    }

    @Test
    public void When_GenerateIdAfter9999999_Then_CounterReset() {
        setCounter(TRANSACTION_KEY, 9999999L);

        assertThat(sequenceService.generateTransactionId()).isEqualTo(1L);
    }

    @Test
    public void When_GenerateDifferentId_Then_Expect_SeparateSequence() {
        resetCounter(TRANSACTION_KEY);
        resetCounter(INTERCHANGE_KEY_1);
        resetCounter(INTERCHANGE_MESSAGE_KEY_1);

        assertThat(sequenceService.generateTransactionId()).isEqualTo(1L);
        assertThat(sequenceService.generateInterchangeId(SENDER_1, RECIPIENT_1)).isEqualTo(1L);
        assertThat(sequenceService.generateMessageId(SENDER_1, RECIPIENT_1)).isEqualTo(1L);
    }

    @Test
    public void When_GenerateInterchangeId_Then_Expect_SeparateSequenceForEachKey() {
        resetCounter(INTERCHANGE_KEY_1);
        resetCounter(INTERCHANGE_KEY_2);

        assertThat(sequenceService.generateInterchangeId(SENDER_1, RECIPIENT_1)).isEqualTo(1L);
        assertThat(sequenceService.generateInterchangeId(SENDER_2, RECIPIENT_2)).isEqualTo(1L);
        assertThat(sequenceService.generateInterchangeId(SENDER_1, RECIPIENT_1)).isEqualTo(2L);
        assertThat(sequenceService.generateInterchangeId(SENDER_2, RECIPIENT_2)).isEqualTo(2L);
    }

    @Test
    public void When_GenerateMessageId_Then_Expect_SeparateSequenceForEachKey() {
        resetCounter(INTERCHANGE_MESSAGE_KEY_1);
        resetCounter(INTERCHANGE_MESSAGE_KEY_2);

        assertThat(sequenceService.generateMessageId(SENDER_1, RECIPIENT_1)).isEqualTo(1L);
        assertThat(sequenceService.generateMessageId(SENDER_2, RECIPIENT_2)).isEqualTo(1L);
        assertThat(sequenceService.generateMessageId(SENDER_1, RECIPIENT_1)).isEqualTo(2L);
        assertThat(sequenceService.generateMessageId(SENDER_2, RECIPIENT_2)).isEqualTo(2L);
    }

    @Test
    public void When_GenerateTransactionIdInParallel_Then_Expect_CorrectValues() {
        resetCounter(TRANSACTION_KEY);

        List<Long> expectedList = LongStream.rangeClosed(1, 100)
                .boxed()
                .collect(Collectors.toList());

        assertThat(generateMultiThreadedSeqList()).isEqualTo(expectedList);
    }

    private void setCounter(String key, Long counter) {
        sequenceDao.save(new OutboundSequenceId(key, counter));
    }

    private void resetCounter(String key) {
        sequenceDao.save(new OutboundSequenceId(key, 0L));
    }

    private List<Long> generateMultiThreadedSeqList() {
        List<Long> seqList = Collections.synchronizedList(new ArrayList<>());

        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            service.submit(() -> {
                seqList.add(sequenceService.generateTransactionId());
            });
        }

        await().atMost(20, SECONDS)
                .untilAsserted(() -> MatcherAssert.assertThat(seqList.size(), Matchers.is(100)));

        return seqList.stream()
                .sorted()
                .collect(Collectors.toList());
    }
}
