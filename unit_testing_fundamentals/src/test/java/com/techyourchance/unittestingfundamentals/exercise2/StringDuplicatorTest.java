package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {

    StringDuplicator stringDuplicator;

    @Before
    public void setUp() throws Exception {
        stringDuplicator = new StringDuplicator();
    }

    @Test
    public void concatenate_emptyString_returnEmptyString() {
        String text = stringDuplicator.duplicate("");
        assertThat(text, is(""));
    }

    @Test
    public void concatenate_singleCharacter_returnRepeatedCharacter() {
        String text = stringDuplicator.duplicate("a");
        assertThat(text, is("aa"));
    }

    @Test
    public void concatenate_text_returnRepeatedText() {
        String text = stringDuplicator.duplicate("Mihai");
        assertThat(text, is("MihaiMihai"));
    }
}