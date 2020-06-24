package uk.nhs.digital.nhsconnect.nhais.parse;

import ca.uhn.fhir.rest.gclient.IGetPage;
import com.google.common.collect.Comparators;
import com.google.common.collect.Streams;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.InterchangeV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.MessageV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EdifactParserV2 {
    public InterchangeV2 parse(String edifact) {
        var wholeInterchangeLines = Arrays.asList(Split.bySegmentTerminator(edifact.replaceAll("\\n", "").strip()));

        var interchange = parseInterchange(wholeInterchangeLines);

        validateInterchange(interchange);

        return interchange;
    }

    private InterchangeV2 parseInterchange(List<String> wholeInterchangeLines) {
        InterchangeV2 interchange = new InterchangeV2(extractInterchangeLines(wholeInterchangeLines));

        var messages = parseMessages(wholeInterchangeLines);
        messages.forEach(message -> message.setInterchange(interchange));
        interchange.setMessages(messages);

        return interchange;
    }

    private List<Pair<Integer, Integer>> zipIndexes(List<Integer> startIndexes, List<Integer> endIndexes) {
        var indexPairs = Streams.zip(startIndexes.stream(), endIndexes.stream(), Pair::of)
            .collect(Collectors.toList());

        if (startIndexes.size() != endIndexes.size()
            || indexPairs.size() != startIndexes.size()) {
            throw new ToEdifactParsingException(
                "Message header-trailer count mismatch: " + startIndexes.size() + "-" + endIndexes.size());
        }

        if (!areIndexesInOrder(indexPairs)) {
            throw new ToEdifactParsingException("Message trailer before message header");
        }

        return indexPairs;
    }

    private List<MessageV2> parseMessages(List<String> wholeInterchangeLines) {
        var messageHeaderIndexes = findAllIndexes(wholeInterchangeLines, MessageHeader.KEY);
        var messageTrailerIndexes = findAllIndexes(wholeInterchangeLines, MessageTrailer.KEY);

        var messageStartEndIndexPairs = zipIndexes(messageHeaderIndexes, messageTrailerIndexes);

        return messageStartEndIndexPairs.stream()
            .map(messageStartEndIndexPair -> wholeInterchangeLines.subList(messageStartEndIndexPair.getLeft(), messageStartEndIndexPair.getRight() + 1))
            .map(this::parseMessage)
            .collect(Collectors.toList());
    }

    private MessageV2 parseMessage(List<String> wholeMessageLines) {
        var transactionStartIndexes = findAllIndexes(wholeMessageLines, ReferenceTransactionNumber.KEY_QUALIFIER);
        var transactionEndIndexes = new ArrayList<>(transactionStartIndexes);
        transactionEndIndexes.remove(0); // there is no tran end so ending index is the beginning of the next one so end is beginning shifted 1 right
        transactionEndIndexes.add(wholeMessageLines.size() - 1); // to remove message trailer

        var transactionIndexPairs = zipIndexes(transactionStartIndexes, transactionEndIndexes);

        var transactions = transactionIndexPairs.stream()
            .map(transactionIndexPair -> wholeMessageLines.subList(transactionIndexPair.getLeft(), transactionIndexPair.getRight()))
            .map(TransactionV2::new)
            .collect(Collectors.toList());

        var onlyMessageLines = new ArrayList<>(wholeMessageLines.subList(0, transactionStartIndexes.get(0))); // first lines until transaction
        onlyMessageLines.add(wholeMessageLines.get(wholeMessageLines.size() - 1)); // message trailer

        var message = new MessageV2(onlyMessageLines);
        message.setTransactions(transactions);
        transactions.forEach(transaction -> transaction.setMessage(message));

        return message;
    }

    private boolean areIndexesInOrder(List<Pair<Integer, Integer>> messageIndexPairs) {
        return Comparators.isInOrder(
            messageIndexPairs.stream()
                .map(pair -> List.of(pair.getLeft(), pair.getRight()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()),
            Comparator.naturalOrder());
    }

    private List<String> extractInterchangeLines(List<String> wholeInterchangeLines) {

        var firstMessageHeaderIndex = findAllIndexes(wholeInterchangeLines, MessageHeader.KEY).get(0);
        var allMessageTrailerIndexes = findAllIndexes(wholeInterchangeLines, MessageTrailer.KEY);
        var lastMessageTrailerIndex = allMessageTrailerIndexes.get(allMessageTrailerIndexes.size() - 1);

        return Stream.of(
            wholeInterchangeLines.subList(0, firstMessageHeaderIndex),
            wholeInterchangeLines.subList(lastMessageTrailerIndex + 1, wholeInterchangeLines.size()))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<Integer> findAllIndexes(List<String> list, String key) {
        var indexes = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(key)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private void validateInterchange(InterchangeV2 interchange) {
        List<ToEdifactParsingException> exceptions = interchange.validate();
        String errorList = exceptions.stream()
            .map(Exception::getMessage)
            .collect(Collectors.joining(", "));

        if (StringUtils.isNotEmpty(errorList)) {
            throw new ToEdifactParsingException(errorList);
        }
    }
}
