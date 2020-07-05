package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split.byPlus;
import static uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split.bySegmentTerminator;

class SplitTest {
    @Test
    public void When_SplittingBySegmentTerminator_Expect_CorrectResult() {
        assertThat(bySegmentTerminator("a'q")).containsExactly("a", "q");
        assertThat(bySegmentTerminator("a?'q")).containsExactly("a?'q");
        assertThat(bySegmentTerminator("a??'q")).containsExactly("a??", "q");
        assertThat(bySegmentTerminator("a???'q")).containsExactly("a???'q");
        assertThat(bySegmentTerminator("a???''q")).containsExactly("a???'", "q");
        assertThat(bySegmentTerminator("a?b??'?'??''???'q'")).containsExactly("a?b??", "?'??", "", "???'q", "");
        assertThat(bySegmentTerminator("")).containsExactly("");
        assertThat(bySegmentTerminator("'")).containsExactly("", "");
        assertThat(bySegmentTerminator("''")).containsExactly("", "", "");
        assertThat(bySegmentTerminator("?'?'")).containsExactly("?'?'");
        assertThat(bySegmentTerminator("??'??'")).containsExactly("??", "??", "");
        assertThat(bySegmentTerminator("???'???'")).containsExactly("???'???'");
        assertThat(bySegmentTerminator("??")).containsExactly("??");
        assertThat(bySegmentTerminator("'????")).containsExactly("", "????");
        assertThat(bySegmentTerminator("''????")).containsExactly("", "", "????");
        assertThat(bySegmentTerminator("''???'")).containsExactly("", "", "???'");
        assertThat(bySegmentTerminator("''?''")).containsExactly("", "", "?'", "");
    }

    @Test
    public void When_Plus() {
        assertThat(byPlus("++a?b??'?+??++???+'q'")).containsExactly("", "", "a?b??'?+??", "", "???+\\q'");
    }
}