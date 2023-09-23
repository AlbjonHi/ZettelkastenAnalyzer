package com.amarildo.zettelkastenanalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author aaliaj
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Nested
    @DisplayName("extract words from file")
    class ExtractWordsFromFile {

        String testFileName = "test_note.md";
        String testFilePath;

        @Spy
        NoteService noteServiceMock;

        @BeforeEach
        void beforeAll() {
            URL resource = Thread.currentThread().getContextClassLoader().getResource(testFileName);
            testFilePath = resource.getPath().substring(1); // remove '/' before "C:..."
        }

        @Test
        @DisplayName("empty file")
        void emptyFile() throws IOException {
            // given
            when(noteServiceMock.readContentFile(testFilePath)).thenReturn("");
            List<String> expected = new ArrayList<>();

            // when
            List<String> actual = noteServiceMock.extractWordsFromFile(testFilePath);

            // then
            verify(noteServiceMock).readContentFile(testFilePath);
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("file with a single word")
        void fileWithASingleWord() throws IOException {
            // given
            when(noteServiceMock.readContentFile(testFilePath)).thenReturn("word");
            List<String> expected = List.of("word");

            // when
            List<String> actual = noteServiceMock.extractWordsFromFile(testFilePath);

            // then
            verify(noteServiceMock).readContentFile(testFilePath);
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("file with many words")
        void fileWithManyWords() throws IOException {
            // given
            when(noteServiceMock.readContentFile(testFilePath)).thenReturn("first second third");
            List<String> expected = List.of("first", "second", "third");

            // when
            List<String> actual = noteServiceMock.extractWordsFromFile(testFilePath);

            // then
            verify(noteServiceMock).readContentFile(testFilePath);
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("file with punctuations")
        void fileWithPunctuations() throws IOException {
            // given
            when(noteServiceMock.readContentFile(testFilePath)).thenReturn("first; second. third/");
            List<String> expected = List.of("first", "second", "third");

            // when
            List<String> actual = noteServiceMock.extractWordsFromFile(testFilePath);

            // then
            verify(noteServiceMock).readContentFile(testFilePath);
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("file with inline code block in markdown")
        void fileWithInlineCodeBlockInMarkdown() throws IOException {
            // given
            when(noteServiceMock.readContentFile(testFilePath)).thenReturn("`code1` hello `code2`");
            List<String> expected = List.of("code1", "hello", "code2");

            // when
            List<String> actual = noteServiceMock.extractWordsFromFile(testFilePath);

            // then
            verify(noteServiceMock).readContentFile(testFilePath);
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("file with several lines")
        void fileWithSeveralLines() throws IOException {
            // given
            when(noteServiceMock.readContentFile(testFilePath)).thenReturn("hi\r\n how \r\n code2 ");
            List<String> expected = List.of("hi", "how", "code2");

            // when
            List<String> actual = noteServiceMock.extractWordsFromFile(testFilePath);

            // then
            verify(noteServiceMock).readContentFile(testFilePath);
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("file with non ascii value")
        void fileWithNonAsciiValue() throws IOException {
            // given
            when(noteServiceMock.readContentFile(testFilePath)).thenReturn("✅ how");
            List<String> expected = List.of("✅", "how");

            // when
            List<String> actual = noteServiceMock.extractWordsFromFile(testFilePath);

            // then
            verify(noteServiceMock).readContentFile(testFilePath);
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("io exception with wrong file path")
        void ioExceptionWithWrongFilePath() {
            // given
            String path = "nolan"; // wrong path
            Class<? extends Throwable> expected = IOException.class;

            // when
            Executable actual = () -> noteServiceMock.extractWordsFromFile(path);

            // then
            assertThrows(expected, actual);
        }

    }
}
