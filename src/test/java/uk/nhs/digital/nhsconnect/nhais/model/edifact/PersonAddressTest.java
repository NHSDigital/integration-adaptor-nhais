package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonAddressTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT'";

        var personAddress = PersonAddress.builder()
            .addressLine1("MOORSIDE FARM")
            .addressLine2("OLD LANE")
            .addressLine3("ST PAULS CRAY")
            .addressLine4("ORPINGTON")
            .addressLine5("KENT")
            .build();

        assertEquals(expectedValue, personAddress.toEdifact());
    }

    @Test
    public void When_MappingToEdifacWithPostcodet_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PAT++HIGHFIELD HOUSE:LOW PASS:HAYFIELD HAMLET:GRASSFUL:FIELDING+++++HR3  5BW'";

        var personAddress = PersonAddress.builder()
            .addressLine1("HIGHFIELD HOUSE")
            .addressLine2("LOW PASS")
            .addressLine3("HAYFIELD HAMLET")
            .addressLine4("GRASSFUL")
            .addressLine5("FIELDING")
            .postalCode("HR3  5BW")
            .build();

        assertEquals(expectedValue, personAddress.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithMissingFields_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PAT++:MOORSIDE FARM:ST PAULS CRAY::KENT'";

        var personAddress = PersonAddress.builder()
            .addressLine2("MOORSIDE FARM")
            .addressLine3("ST PAULS CRAY")
            .addressLine5("KENT")
            .build();

        assertEquals(expectedValue, personAddress.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithoutMandatoryAddressLines_Then_EdifactValidationExceptionIsThrown() {
        var personAddress = PersonAddress.builder()
            .addressLine3("test value")
            .build();

        assertThrows(EdifactValidationException.class, personAddress::toEdifact);
    }

    @Test
    public void When_MappingToEdifactWithBlankMandatoryAddressLines_Then_EdifactValidationExceptionIsThrown() {
        var personAddress = PersonAddress.builder()
            .addressLine1("")
            .addressLine2("   ")
            .build();

        assertThrows(EdifactValidationException.class, personAddress::toEdifact);
    }
}
