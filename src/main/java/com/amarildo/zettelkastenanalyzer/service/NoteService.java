package com.amarildo.zettelkastenanalyzer.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class NoteService {

    // TODO (21/09/2023): method for parsing a folder hierarchy recursively file by file

    // TODO (21/09/2023): method to extract all the words contained in a file

    // TODO (22/09/2023): creating a story about the progress of the zettelkasten. i want to display graphs that show the trend over time based on the chosen filters

    // TODO (22/09/2023): find a way to distinguish completed notes from uncompleted ones

    // TODO (23/09/2023): method get note category, priority and notesAddedToAnki from the first lines of the note
    // a generic file starts like this:
    // line 1. -> [[CATEGORY_MOC]]
    // line 2. -> [[Priority = ✅]] (✅ = COMPLETED, 🔝 = HIGH, 🛠️ = MEDIUM, ⏳ = LOW)
    // line 3. -> [[Anki = ⛔]]
    // not all notes have this format. manage the eventuality

    /**
     * Extracts words from a specified file and returns them as a list of strings.
     *
     * @param filePath The path of the file from which to extract words.
     * @return A list of strings containing the words extracted from the file.
     * @throws IOException If an error occurs while reading the file.
     */
    public List<String> extractWordsFromFile(String filePath) throws IOException {
        String fileContent = readContentFile(filePath);

        if (fileContent.isEmpty()) {
            return List.of();
        }

        return Stream.of(fileContent.split("\\s+"))
                .map(word -> word.replaceAll("[\\p{Punct}']", "")) // Combine both replacements
                .toList();
    }

    /**
     * Reads the content of a file and returns it as a string.
     *
     * @param filePath The path to the file to be read.
     * @return The content of the file as a string.
     * @throws IOException If an I/O error occurs or if the file does not exist or is a directory.
     */
    String readContentFile(String filePath) throws IOException {

        Path path = Path.of(filePath);

        // Check if the file exists and is not a directory
        if (!Files.isRegularFile(path)) {
            throw new IOException("The file does not exist or is a directory");
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public boolean isFileChanged(File f) {
        return false;
    }
}
