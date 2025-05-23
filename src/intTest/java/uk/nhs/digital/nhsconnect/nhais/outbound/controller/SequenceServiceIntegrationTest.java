package uk.nhs.digital.nhsconnect.nhais.outbound.controller;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.sequence.OutboundSequenceId;
import uk.nhs.digital.nhsconnect.nhais.sequence.SequenceDao;
import uk.nhs.digital.nhsconnect.nhais.sequence.SequenceService;

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

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@DirtiesContext
public class SequenceServiceIntegrationTest {
    private static final String SENDER_1 = "test-sender-1";
    private static final String SENDER_2 = "test-sender-2";
    private static final String RECIPIENT_1 = "test-recipient-1";
    private static final String RECIPIENT_2 = "test-recipient-2";
    private static final String TRANSACTION_SENDER = "gp-sender";
    private static final String TRANSACTION_KEY = String.format("TN-%s", TRANSACTION_SENDER);
    private static final String INTERCHANGE_KEY_1 = String.format("SIS-%s-%s", SENDER_1, RECIPIENT_1);
    private static final String INTERCHANGE_KEY_2 = String.format("SIS-%s-%s", SENDER_2, RECIPIENT_2);
    private static final String INTERCHANGE_MESSAGE_KEY_1 = String.format("SMS-%s-%s", SENDER_1, RECIPIENT_1);
    private static final String INTERCHANGE_MESSAGE_KEY_2 = String.format("SMS-%s-%s", SENDER_2, RECIPIENT_2);
    private static final long SEQUENCE_NUMBER_1 = 1L;
    private static final long SEQUENCE_NUMBER_2 = 2L;
    private static final long SEQUENCE_NUMBER_3 = 3L;
    private static final long MAXIMUM_ID_VALUE = 9_999_999L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SequenceService sequenceService;
    @Autowired
    private SequenceDao sequenceDao;

    @Test
    public void When_GenerateTransactionId_Expect_IncreasedByOne() {
        resetCounter(TRANSACTION_SENDER);

        assertThat(sequenceService.generateTransactionNumber(TRANSACTION_SENDER)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateTransactionNumber(TRANSACTION_SENDER)).isEqualTo(SEQUENCE_NUMBER_2);
        assertThat(sequenceService.generateTransactionNumber(TRANSACTION_SENDER)).isEqualTo(SEQUENCE_NUMBER_3);
    }

    @Test
    public void When_GenerateInterchangeId_Expect_IncreasedByOne() {
        resetCounter(INTERCHANGE_KEY_1);

        assertThat(sequenceService.generateInterchangeSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateInterchangeSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_2);
        assertThat(sequenceService.generateInterchangeSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_3);
    }

    @Test
    public void When_GenerateMessageId_Expect_IncreasedByOne() {
        resetCounter(INTERCHANGE_MESSAGE_KEY_1);

        assertThat(sequenceService.generateMessageSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateMessageSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_2);
        assertThat(sequenceService.generateMessageSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_3);
    }

    @Test
    public void When_GenerateIdGreaterThan9999999_Expect_CounterReset() {
        setCounter(TRANSACTION_KEY, MAXIMUM_ID_VALUE);

        assertThat(sequenceService.generateTransactionNumber(TRANSACTION_SENDER)).isEqualTo(SEQUENCE_NUMBER_1);
    }

    @Test
    public void When_GenerateDifferentId_Expect_Expect_SeparateSequence() {
        resetCounter(TRANSACTION_KEY);
        resetCounter(INTERCHANGE_KEY_1);
        resetCounter(INTERCHANGE_MESSAGE_KEY_1);

        assertThat(sequenceService.generateTransactionNumber(TRANSACTION_SENDER)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateInterchangeSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateMessageSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_1);
    }

    @Test
    public void When_GenerateInterchangeId_Expect_Expect_SeparateSequenceForEachKey() {
        resetCounter(INTERCHANGE_KEY_1);
        resetCounter(INTERCHANGE_KEY_2);

        assertThat(sequenceService.generateInterchangeSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateInterchangeSequence(SENDER_2, RECIPIENT_2)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateInterchangeSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_2);
        assertThat(sequenceService.generateInterchangeSequence(SENDER_2, RECIPIENT_2)).isEqualTo(SEQUENCE_NUMBER_2);
    }

    @Test
    public void When_GenerateMessageId_Expect_Expect_SeparateSequenceForEachKey() {
        resetCounter(INTERCHANGE_MESSAGE_KEY_1);
        resetCounter(INTERCHANGE_MESSAGE_KEY_2);

        assertThat(sequenceService.generateMessageSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateMessageSequence(SENDER_2, RECIPIENT_2)).isEqualTo(SEQUENCE_NUMBER_1);
        assertThat(sequenceService.generateMessageSequence(SENDER_1, RECIPIENT_1)).isEqualTo(SEQUENCE_NUMBER_2);
        assertThat(sequenceService.generateMessageSequence(SENDER_2, RECIPIENT_2)).isEqualTo(SEQUENCE_NUMBER_2);
    }

    @Test
    public void When_GenerateTransactionIdInParallel_Expect_Expect_CorrectValues() {
        resetCounter(TRANSACTION_KEY);
        final long minimumRange = 1;
        final long maximumRange = 100;

        List<Long> expectedList = LongStream.rangeClosed(minimumRange, maximumRange)
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
        final int numberOfThreads = 10;
        final int sequenceCount = 100;
        final int awaitDelay = 20;

        List<Long> seqList = Collections.synchronizedList(new ArrayList<>());

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < sequenceCount; i++) {
            service.submit(() -> {
                seqList.add(sequenceService.generateTransactionNumber(TRANSACTION_SENDER));
            });
        }

        await().atMost(awaitDelay, SECONDS)
            .untilAsserted(() -> MatcherAssert.assertThat(seqList.size(), Matchers.is(sequenceCount)));

        return seqList.stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
