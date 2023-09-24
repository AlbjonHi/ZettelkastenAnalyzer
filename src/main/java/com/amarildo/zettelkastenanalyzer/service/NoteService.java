package com.amarildo.zettelkastenanalyzer.service;

import com.amarildo.zettelkastenanalyzer.model.Note;
import com.amarildo.zettelkastenanalyzer.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
public class NoteService {

    NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // TODO (22/09/2023): creating a story about the progress of the zettelkasten. i want to display graphs that show the trend over time based on the chosen filters

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
                    Optional<Note> optionalNote = extractInfoFromFile(file);
                    if (optionalNote.isPresent()) {
                        log.info("Il file {} e' cambiato", file);
                        noteRepository.save(optionalNote.get());
                    } else {
                        log.info("Il file {} non e' cambiato", file);
                    }
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
        System.out.println("Done!");
    }

    /**
     * Calculates the SHA-256 hash of a file located at the specified file path.
     *
     * @param filePath The path to the file for which the hash needs to be calculated.
     * @return A hexadecimal string representing the SHA-256 hash of the file, or null
     * if an error occurs during the calculation.
     */
    private String calculateFileHash(String filePath) {
        String algorithm = "SHA-256";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
            messageDigest.update(fileBytes);
            byte[] mdBytes = messageDigest.digest();
            return bytesToHex(mdBytes);
        } catch (NoSuchAlgorithmException nsae) {
            log.warn("The chosen algorithm ({}) does not exist", algorithm, nsae);
        } catch (IOException ioe) {
            log.warn("File ({}) reading error", filePath, ioe);
        }
        return null;
    }

    /**
     * Converts an array of bytes into a hexadecimal string representation.
     *
     * @param bytes The array of bytes to be converted.
     * @return A hexadecimal string representation of the input byte array.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte mdByte : bytes) {
            String hex = Integer.toHexString(0xFF & mdByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Checks if a file has been changed by comparing its hash with the stored hash in the repository.
     *
     * @param filePath The path of the file to check.
     * @return True if the file has changed, false otherwise (or if it doesn't exist in the repository).
     */
    public boolean isFileChanged(Path filePath) {
        String fileName = getFileNameFromPath(filePath);
        return noteRepository.getNoteByFileName(fileName)
                .map(oldNote -> !oldNote.getHash().equals(calculateFileHash(filePath.toString())))
                .orElse(true);
    }

    /**
     * Extracts the file name from a given file path.
     *
     * @param filePath The path of the file.
     * @return The file name extracted from the path.
     */
    private String getFileNameFromPath(Path filePath) {
        return filePath.getFileName().toString();
    }

    // TODO (24/09/2023): to be completed
    public Optional<Note> extractInfoFromFile(Path filePath) {

        boolean fileChanged = isFileChanged(filePath);
        if (!fileChanged) {
            return Optional.empty();
        }

        // get file name
        String fileName = getFileNameFromPath(filePath)
                .split("\\.")[0]; // without '.md'

        // hash
        String hash = calculateFileHash(filePath.toString());

        // words
        Set<String> uniqueWords = new HashSet<>();
        try {
            List<String> words = extractWordsFromFile(filePath.toString());
            uniqueWords.addAll(words);
        } catch (IOException ioe) {
            log.warn("File ({}) reading error", filePath, ioe);
        }

        // get category

        // get priority'

        // get anki

        return Optional.of(new Note(fileName, hash, uniqueWords, null, null, false));
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

        return Stream.of(fileContent.split("[[#,.\\[\\]/()`!\\\"$%*}{\\\\_':;=?\\-<>+|\\r\\n\\s][0-9]]"))
                .map(String::toLowerCase)
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
}
