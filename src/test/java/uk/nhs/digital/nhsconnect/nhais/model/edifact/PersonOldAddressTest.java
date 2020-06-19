package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonOldAddressTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PER++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT'";

        var personOldAddress = PersonOldAddress.builder()
            .addressLine1("MOORSIDE FARM")
            .addressLine2("OLD LANE")
            .addressLine3("ST PAULS CRAY")
            .addressLine4("ORPINGTON")
            .addressLine5("KENT")
            .build();

        assertEquals(expectedValue, personOldAddress.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithMissingFields_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PER++MOORSIDE FARM:ST PAULS CRAY:KENT'";

        var personOldAddress = PersonOldAddress.builder()
            .addressLine1("MOORSIDE FARM")
            .addressLine3("ST PAULS CRAY")
            .addressLine5("KENT")
            .build();

        assertEquals(expectedValue, personOldAddress.toEdifact());
    }

}
