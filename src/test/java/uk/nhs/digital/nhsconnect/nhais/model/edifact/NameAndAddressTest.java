package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameAndAddressTest {

    @Test
    public void testValidMessageHeader() throws EdifactValidationException {
        NameAndAddress nameAndAddress = new NameAndAddress("PARTY",NameAndAddress.QualifierAndCode.FHS);

        String edifact = nameAndAddress.toEdifact();

        assertEquals("NAD+FHS+PARTY:954'", edifact);
    }

    @Test
    public void testValidationStatefulNonSequenceNumber() {
        NameAndAddress nameAndAddress = new NameAndAddress("",NameAndAddress.QualifierAndCode.FHS);

        Exception exception = assertThrows(EdifactValidationException.class, nameAndAddress::preValidate);

        String expectedMessage = ": Attribute identifier is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testFromString() {
        var expectedNameAndAddress = new NameAndAddress("HA456", NameAndAddress.QualifierAndCode.FHS);
        var edifact = "NAD+FHS+HA456:954'";

        var nameAndAddress = NameAndAddress.fromString(edifact);

        assertThat(nameAndAddress).isExactlyInstanceOf(NameAndAddress.class);
        assertThat(nameAndAddress).isEqualToComparingFieldByField(expectedNameAndAddress);
        assertThat(nameAndAddress.toEdifact()).isEqualTo(edifact);
    }
}
