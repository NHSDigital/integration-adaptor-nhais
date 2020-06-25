package uk.nhs.digital.nhsconnect.nhais.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.InterchangeV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.MessageV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Comparators;
import com.google.common.collect.Streams;

public class MessageSegments {
    private final List<String> edifactSegments;

    public MessageSegments(List<String> edifactSegments) {
        this.edifactSegments = List.copyOf(edifactSegments);
    }

    public List<MessageV2> toMessages(InterchangeV2 interchange) {
        var messageHeaderIndexes = EdifactParserV2.findAllIndexes(edifactSegments, MessageHeader.KEY);
        var messageTrailerIndexes = EdifactParserV2.findAllIndexes(edifactSegments, MessageTrailer.KEY);

        var messageStartEndIndexPairs = EdifactParserV2.zipIndexes(messageHeaderIndexes, messageTrailerIndexes);

        return messageStartEndIndexPairs.stream()
            .map(messageStartEndIndexPair -> edifactSegments.subList(messageStartEndIndexPair.getLeft(), messageStartEndIndexPair.getRight() + 1))
            .map(segments -> parseMessage(segments, interchange))
            .collect(Collectors.toList());
    }

   private MessageV2 parseMessage(List<String> edifactSegments, InterchangeV2 interchange) {
        TransactionSegments transactionSegments = new TransactionSegments(edifactSegments);

       var onlyMessageSegments = extractMessageSegments(edifactSegments, transactionSegments);

       return new MessageV2(interchange, onlyMessageSegments, transactionSegments);
    }

    private ArrayList<String> extractMessageSegments(List<String> edifactSegments, TransactionSegments transactionSegments) {
        var onlyMessageLines = new ArrayList<>(edifactSegments.subList(0, transactionSegments.getFirstTransactionIndex())); // first lines until transaction
        onlyMessageLines.add(edifactSegments.get(edifactSegments.size() - 1)); // message trailer
        return onlyMessageLines;
    }
}
