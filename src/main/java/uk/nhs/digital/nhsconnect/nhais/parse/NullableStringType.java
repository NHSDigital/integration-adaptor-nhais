package uk.nhs.digital.nhsconnect.nhais.parse;

import org.hl7.fhir.r4.model.StringType;

public class NullableStringType extends StringType {
    public NullableStringType(String theValue) {
        super(theValue);
    }

    public NullableStringType() {
        super(null);
    }

    @Override
    public String getId() {
        return "nullable-string";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
