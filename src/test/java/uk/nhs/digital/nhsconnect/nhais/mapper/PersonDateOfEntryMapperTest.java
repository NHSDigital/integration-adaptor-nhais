package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfEntry;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDateOfEntryMapperTest {
    private static final Instant FIXED_TIME = ZonedDateTime.of(
            1991,
            11,
            6,
            23,
            55,
            0,
            0,
            ZoneId.of("Europe/London")).toInstant();


    @Test
    void When_MappingDateOfEntry_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName("entryDate")
                .setValue(new StringType(FIXED_TIME.toString()));

        var personDateOfEntryMapper = new PersonDateOfEntryMapper();
        PersonDateOfEntry personDateOfEntry = personDateOfEntryMapper.map(parameters);

        var expectedPersonDateOfEntry = PersonDateOfEntry
                .builder()
                .timestamp(FIXED_TIME)
                .build();

        assertEquals(expectedPersonDateOfEntry, personDateOfEntry);

    }

    @Test
    public void When_MappingWithWrongDate_Then_DateTimeParseExceptionIsThrown() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName("entryDate")
                .setValue(new StringType(""));

        var personDateOfEntryMapper = new PersonDateOfEntryMapper();
        assertThrows(DateTimeParseException.class, () -> personDateOfEntryMapper.map(parameters));
    }

    @Test
    public void When_MappingWithoutDateParam_Then_NoSuchElementExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personDateOfEntryMapper = new PersonDateOfEntryMapper();
        assertThrows(NoSuchElementException.class, () -> personDateOfEntryMapper.map(parameters));
    }
}
