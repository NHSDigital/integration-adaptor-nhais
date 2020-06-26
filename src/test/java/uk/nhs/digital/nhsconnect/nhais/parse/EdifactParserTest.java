package uk.nhs.digital.nhsconnect.nhais.parse;

import com.google.common.collect.Streams;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Section;

class EdifactParserTest {

    @SuppressWarnings("UnstableApiUsage")
    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.class)
    void testEdifactParserSplitsSegmentsIntoProperSections(String test, CustomArgumentsProvider.TestData testData) {
        Interchange interchange = new EdifactParser().parse(testData.getInput());

        SoftAssertions.assertSoftly(softly -> {

            var actualInterchangeSegments = interchange.getEdifactSegments();
            var expectedInterchangeSegments = testData.getInterchange().getEdifactSegments();
            softly.assertThat(actualInterchangeSegments).isEqualTo(expectedInterchangeSegments);

            var actualMessages = interchange.getMessages();
            var expectedMessages = testData.getInterchange().getMessages();
            softly.assertThat(actualMessages).hasSameSizeAs(expectedMessages);

            Streams.zip(actualMessages.stream(), expectedMessages.stream(), Pair::of)
                .forEach(messagePair -> {
                    var actualMessageSegments = messagePair.getLeft().getEdifactSegments();
                    var expectedMessageSegments = messagePair.getRight().getEdifactSegments();
                    softly.assertThat(actualMessageSegments).isEqualTo(expectedMessageSegments);

                    var actualTransactions = messagePair.getLeft().getTransactions();
                    var expectedTransactions = messagePair.getRight().getTransactions();
                    softly.assertThat(actualTransactions).hasSameSizeAs(expectedTransactions);

                    Streams.zip(
                        actualTransactions.stream().map(Section::getEdifactSegments),
                        expectedTransactions.stream().map(CustomArgumentsProvider.Transaction::getEdifactSegments),
                        Pair::of)
                        .forEach(transactionPair -> softly.assertThat(transactionPair.getLeft()).isEqualTo(transactionPair.getRight()));
                });
        });
    }
}
