package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

import java.util.ArrayList;
import java.util.List;

public class Split {
    private static final char ESC_CHAR = '?';
    private static final char SEGMENT_TERMINATOR = '\'';
    private static final char FIELD_TERMINATOR = '+';
    private static final char SUB_FIELD_TERMINATOR = ':';

    public static String[] bySegmentTerminator(String input) {
        return tokenizeString(input, SEGMENT_TERMINATOR);
    }

    public static String[] byPlus(String input) {
        return tokenizeString(input, FIELD_TERMINATOR);
    }

    public static String[] byColon(String input) {
        return tokenizeString(input, SUB_FIELD_TERMINATOR);
    }

    public static String[] tokenizeString(String s, char sep) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        int escChars = 0;
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            if (c == ESC_CHAR) {
                escChars++;
            } else if (c == sep && escChars % 2 == 0) {
                escChars = 0;
                tokens.add(sb.toString());
                sb.setLength(0);
                continue;
            } else {
                escChars = 0;
            }

            sb.append(c);
        }
        tokens.add(sb.toString());

        String[] array = new String[tokens.size()];
        tokens.toArray(array);
        return array;
    }
}
