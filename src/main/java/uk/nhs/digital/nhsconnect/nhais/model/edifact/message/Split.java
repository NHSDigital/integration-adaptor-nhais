package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

public class Split {
    public static String[] byColon(String input){
        return input.split(":");
    }
    public static String[] byPlus(String input){
        return input.split("\\+");
    }
    /**
     * Matches an apostrophe NOT preceded by a question mark
     */
    public static String[] bySegmentTerminator(String input){
        return input.split("((?<!\\?)')");
    }

}
