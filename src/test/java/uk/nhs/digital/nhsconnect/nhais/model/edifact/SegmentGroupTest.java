package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SegmentGroupTest {

    @Test
    public void testValidSegmentGroup() throws EdifactValidationException {
        SegmentGroup segmentGroup1 = new SegmentGroup(1);
        SegmentGroup segmentGroup2 = new SegmentGroup(2);

        String edifact1 = segmentGroup1.toEdifact();
        String edifact2 = segmentGroup2.toEdifact();

        assertEquals("S01+1'", edifact1);
        assertEquals("S02+2'", edifact2);
    }

    @Test
    public void testValidationStateful() {
        SegmentGroup segmentGroup = new SegmentGroup(3);

        Exception exception = assertThrows(EdifactValidationException.class, segmentGroup::preValidate);

        String expectedMessage = "S: Attribute segment_group_number must be 1 or 2";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
