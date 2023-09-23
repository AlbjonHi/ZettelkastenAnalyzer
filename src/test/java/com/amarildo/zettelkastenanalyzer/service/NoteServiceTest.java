package com.amarildo.zettelkastenanalyzer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        @DisplayName("empty file")
        void emptyFile() throws IOException {
            // given
            when(noteService.readContentFile(filePath)).thenReturn("");
            List<String> expected = new ArrayList<>();

            // when
            List<String> actual = noteService.extractWordsFromFile(filePath);

            // then
            verify(noteService).readContentFile(filePath);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("file with a single word")
        void fileWithASingleWord() throws IOException {
            // given
            when(noteService.readContentFile(filePath)).thenReturn("word");
            List<String> expected = List.of("word");

            // when
            List<String> actual = noteService.extractWordsFromFile(filePath);

            // then
            verify(noteService).readContentFile(filePath);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("file with many words")
        void fileWithManyWords() throws IOException {
            // given
            when(noteService.readContentFile(filePath)).thenReturn("first second third");
            List<String> expected = List.of("first", "second", "third");

            // when
            List<String> actual = noteService.extractWordsFromFile(filePath);

            // then
            verify(noteService).readContentFile(filePath);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("file with punctuations")
        void fileWithPunctuations() throws IOException {
            // given
            when(noteService.readContentFile(filePath)).thenReturn("first; second. third/");
            List<String> expected = List.of("first", "second", "third");

            // when
            List<String> actual = noteService.extractWordsFromFile(filePath);

            // then
            verify(noteService).readContentFile(filePath);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("file with inline code block in markdown")
        void fileWithInlineCodeBlockInMarkdown() throws IOException {
            // given
            when(noteService.readContentFile(filePath)).thenReturn("`code1` hello `code2`");
            List<String> expected = List.of("code1", "hello", "code2");

            // when
            List<String> actual = noteService.extractWordsFromFile(filePath);

            // then
            verify(noteService).readContentFile(filePath);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("file with several lines")
        void fileWithSeveralLines() throws IOException {
            // given
            when(noteService.readContentFile(filePath)).thenReturn("hi\r\n how \r\n code2 ");
            List<String> expected = List.of("hi", "how", "code2");

            // when
            List<String> actual = noteService.extractWordsFromFile(filePath);

            // then
            verify(noteService).readContentFile(filePath);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("file with non ascii value")
        void fileWithNonAsciiValue() throws IOException {
            // given
            when(noteService.readContentFile(filePath)).thenReturn("✅ how");
            List<String> expected = List.of("✅", "how");

            // when
            List<String> actual = noteService.extractWordsFromFile(filePath);

            // then
            verify(noteService).readContentFile(filePath);
            assertEquals(expected, actual);
        }

    }
}
