package com.amarildo.zettelkastenanalyzer.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class NoteService {

    // TODO (21/09/2023): method to extract all the words contained in a file

    // TODO (22/09/2023): creating a story about the progress of the zettelkasten. i want to display graphs that show the trend over time based on the chosen filters

    // TODO (22/09/2023): find a way to distinguish completed notes from uncompleted ones

    // TODO (23/09/2023): method get note category, priority and notesAddedToAnki from the first lines of the note
    // a generic file starts like this:
    // line 1. -> [[CATEGORY_MOC]]
    // line 2. -> [[Priority = ✅]] (✅ = COMPLETED, 🔝 = HIGH, 🛠️ = MEDIUM, ⏳ = LOW)
    // line 3. -> [[Anki = ⛔]]
    // not all notes have this format. manage the eventuality

    public void fileVisitorOnTree(String rootDir, List<String> skipFolders) throws IOException {
        Path startingDir = Paths.get(rootDir);

        Files.walkFileTree(startingDir, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".md")) {
                    log.info("Markdown File: " + file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String folderName = dir.getFileName().toString();

                if (skipFolders.contains(folderName)) {
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                log.error("Error visiting file: " + file + " - " + exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });
    }

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
