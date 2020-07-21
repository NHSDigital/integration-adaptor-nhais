package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import org.hl7.fhir.r4.model.StringType;

class NullableStringType extends StringType {
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
