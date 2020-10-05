package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        assertThat(personOldAddress.toEdifact()).isEqualTo(expectedValue);
    }

    @Test
    public void When_MappingToEdifactWithMissingFields_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PER++MOORSIDE FARM:ST PAULS CRAY:KENT'";

        var personOldAddress = PersonOldAddress.builder()
            .addressLine1("MOORSIDE FARM")
            .addressLine2(null)
            .addressLine3("ST PAULS CRAY")
            .addressLine4("")
            .addressLine5("KENT")
            .build();

        assertThat(personOldAddress.toEdifact()).isEqualTo(expectedValue);
    }

    @Test
    void When_MappingEmptyAddress_Then_ThrowException() {
        var personOldAddress = PersonOldAddress.builder()
            .build();

        assertThatThrownBy(personOldAddress::toEdifact)
            .isExactlyInstanceOf(EdifactValidationException.class);
    }
}
