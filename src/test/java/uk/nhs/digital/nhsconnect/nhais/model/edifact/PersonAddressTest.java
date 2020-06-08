package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonAddressTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        //NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7EW'
        var expectedValue = "NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT'";

        var personAddress = PersonAddress.builder()
                .addressText("Moorside Farm, Old Lane, St Pauls Cray, Orpington, Kent")
                .addressLine1("Moorside Farm")
                .build();

        assertEquals(expectedValue, personAddress.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyAddressLines_Then_EdifactValidationExceptionIsThrown() {
        var personAddress = PersonAddress.builder()
                .addressText("test value")
                .build();

        assertThrows(EdifactValidationException.class, personAddress::toEdifact);
    }

    @Test
    public void When_MappingToEdifactWithBlankAddressLines_Then_EdifactValidationExceptionIsThrown() {
        var personAddress = PersonAddress.builder()
                .addressText("test value")
                .addressLine1("")
                .addressLine2("   ")
                .build();

        assertThrows(EdifactValidationException.class, personAddress::toEdifact);
    }

    @Test
    public void When_BuildingWithoutMandatoryFields_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonAddress.builder().build());
    }
}
