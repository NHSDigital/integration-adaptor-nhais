package uk.nhs.digital.nhsconnect.nhais.parse;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Comparators;
import com.google.common.collect.Streams;

class TransactionList extends EdifactList<Transaction> {

    private final List<Transaction> transactions;

    public TransactionList(List<String> singleMessageEdifactSegments) {
        var transactionStartIndexes = findAllIndexesOfSegment(singleMessageEdifactSegments, SegmentGroup.KEY_01);
        var transactionEndIndexes = new ArrayList<>(transactionStartIndexes);

        // there might be no transactions inside - RECEP
        if (!transactionEndIndexes.isEmpty()) {
            // there is no transaction end indicator, so ending segment is the one before the beginning of the next transaction
            // so end indexes are beginning without first S01
            transactionEndIndexes.remove(0);
            // and last transaction end indicator is the segment before message trailer
            transactionEndIndexes.add(singleMessageEdifactSegments.size() - 1);
        }

        var transactionStartEndIndexPairs = zipIndexes(transactionStartIndexes, transactionEndIndexes);

        this.transactions = transactionStartEndIndexPairs.stream()
            .map(transactionStartEndIndexPair ->
                singleMessageEdifactSegments.subList(transactionStartEndIndexPair.getLeft(), transactionStartEndIndexPair.getRight()))
            .map(Transaction::new)
            .collect(Collectors.toList());
    }

    @Override
    public Transaction get(int index) {
        return transactions.get(index);
    }

    @Override
    public int size() {
        return transactions.size();
    }
}
