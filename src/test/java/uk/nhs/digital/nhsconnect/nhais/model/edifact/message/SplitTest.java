package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split.byColon;
import static uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split.byPlus;
import static uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split.bySegmentTerminator;

class SplitTest {
    @Test
    public void When_SplittingBySegmentTerminator_Expect_CorrectResult() {
        assertEquals("asdf", bySegmentTerminator("asdf'test-string")[0]);
        assertEquals("asdf?'test-string", bySegmentTerminator("asdf?'test-string")[0]);
        assertEquals("asdf??", bySegmentTerminator("asdf??'test-string")[0]);
        assertEquals("asdf???'test-string", bySegmentTerminator("asdf???'test-string")[0]);
        assertEquals("asdf????", bySegmentTerminator("asdf????'test-string")[0]);
    }

    @Test
    public void When_SplittingByColon_Expect_CorrectResult() {
        assertEquals("asdf", byColon("asdf:test-string")[0]);
        assertEquals("asdf?:test-string", byColon("asdf?:test-string")[0]);
        assertEquals("asdf??", byColon("asdf??:test-string")[0]);
        assertEquals("asdf???:test-string", byColon("asdf???:test-string")[0]);
        assertEquals("asdf????", byColon("asdf????:test-string")[0]);
    }

    @Test
    public void When_SplittingByPlus_Expect_CorrectResult() {
        assertEquals("asdf", byPlus("asdf+test-string")[0]);
        assertEquals("asdf?+test-string", byPlus("asdf?+test-string")[0]);
        assertEquals("asdf??", byPlus("asdf??+test-string")[0]);
        assertEquals("asdf???+test-string", byPlus("asdf???+test-string")[0]);
        assertEquals("asdf????", byPlus("asdf????+test-string")[0]);
    }
}