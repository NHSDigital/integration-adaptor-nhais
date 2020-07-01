package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

public class Split {
    public static String[] byColon(String input) {
        return splitByDelimiter(input, ":");
    }

    public static String[] byPlus(String input) {
        return splitByDelimiter(input, "\\+");
    }

    public static String[] bySegmentTerminator(String input) {
        return splitByDelimiter(input, "'");
    }

    /**
     * Matches given delimiter NOT preceded by a odd occurrence of question mark
     * Works for 1 & 3, Lookbehinds must be fixed-width.
     */
    private static String[] splitByDelimiter(String input, String delimiter) {
        return input.split(String.format("(?<![^\\?]\\?|[^\\?]\\?{3})%s", delimiter));
    }
}
