package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonOldAddressTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PER++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT'";

        var personOldAddress = PersonOldAddress.builder()
            .addressLine1("Moorside Farm")
            .addressLine2("Old Lane")
            .addressLine3("St Pauls Cray")
            .addressLine4("Orpington")
            .addressLine5("Kent")
            .build();

        assertEquals(expectedValue, personOldAddress.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithMissingFields_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PER++MOORSIDE FARM:ST PAULS CRAY:KENT'";

        var personOldAddress = PersonOldAddress.builder()
            .addressLine1("Moorside Farm")
            .addressLine3("St Pauls Cray")
            .addressLine5("Kent")
            .build();

        assertEquals(expectedValue, personOldAddress.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithoutMandatoryAddressLines_Then_EdifactValidationExceptionIsThrown() {
        var personOldAddress = PersonOldAddress.builder()
            .addressLine3("test value")
            .build();

        assertThrows(EdifactValidationException.class, personOldAddress::toEdifact);
    }

    @Test
    public void When_MappingToEdifactWithBlankMandatoryAddressLines_Then_EdifactValidationExceptionIsThrown() {
        var personOldAddress = PersonOldAddress.builder()
            .addressLine1("")
            .addressLine2("   ")
            .build();

        assertThrows(EdifactValidationException.class, personOldAddress::toEdifact);
    }
}
