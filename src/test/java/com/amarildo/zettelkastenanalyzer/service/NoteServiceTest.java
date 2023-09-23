package com.amarildo.zettelkastenanalyzer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author aaliaj
 */
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Nested
    @DisplayName("extract words from file")
    class ExtractWordsFromFile {

        String filePath = "your_file_path.txt";

        @Mock
        NoteService noteService;

        @Test
        @DisplayName("Test name")
        void testName() {
            // given
            String expected;

            // when
            String actual;

            // then
            fail("Not implemented yet.");
            // Assertions.assertEquals(expected, actual); // TODO (17/07/2023 - aaliaj): Da implementare
        }

// test cases:
        // 1. empty file -> empty list
        // 2. file with single word -> list with only one string
        // 3. file with many words -> list with all the words in the file
        // 4. file with punctuations/spaces -> list without punctuation (including `) but only with words
        // 5. file with several lines -> list with only words
        // 6. file with non-ascii special characters -> list with only the words (with also non-ascii characters)
    }
}
