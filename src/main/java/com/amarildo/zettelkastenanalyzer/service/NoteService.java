package com.amarildo.zettelkastenanalyzer.service;

import com.amarildo.zettelkastenanalyzer.model.Note;
import com.amarildo.zettelkastenanalyzer.model.Priority;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Service
public class NoteService {

    NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Recursively traverses a directory tree starting from the specified root directory,
     * processes Markdown files, and checks for updates in a Zettelkasten system.
     * <p>
     * This method walks the file tree rooted at 'rootDir', invoking 'processMarkdownFile'
     * for each Markdown file encountered. It also allows skipping specified folders based on
     * the 'skipFolders' list.
     *
     * @param rootDir     The root directory from which to start the traversal.
     * @param skipFolders A list of folder names to skip during traversal.
     */
    public void traverseAndCheckZettelkastenTree(String rootDir, List<String> skipFolders) {
        Path startingDir = Paths.get(rootDir);

        try {
            Files.walkFileTree(startingDir, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".md")) {
                        processMarkdownFile(file);
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
                    log.error("Error visiting file: {} - {}", file, exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("Finished checking for updates on the Zettelkasten files");
        } catch (IOException e) {
            log.error("Error while traversing files: {}", e.getMessage());
        }
    }

    /**
     * Processes a Markdown file, extracts information, and saves it to a repository if changed.
     * <p>
     * This method extracts information from the specified Markdown file using 'extractInfoFromFile'
     * and checks if the information has changed. If changed, it logs the update and saves the
     * extracted information to the 'noteRepository'.
     *
     * @param file The path to the Markdown file to process.
     */
    private void processMarkdownFile(Path file) {
        Optional<Note> optionalNote = extractInfoFromFile(file);
        if (optionalNote.isPresent()) {
            log.info("The file {} has changed", file);
            noteRepository.save(optionalNote.get());
        }
    }

    /**
     * Extracts information from a file and creates a Note object.
     * <p>
     * This method reads the contents of the specified file and extracts relevant information,
     * including the file name without extension, a hash of the file content, unique words in the file,
     * a category extracted from the first line, and a priority extracted from the second line.
     *
     * @param filePath The path to the file from which information is to be extracted.
     * @return An Optional containing a Note object with extracted information if successful,
     * or an empty Optional if the file is unchanged or an error occurs during extraction.
     */
    public Optional<Note> extractInfoFromFile(Path filePath) {

        if (!isFileChanged(filePath)) {
            return Optional.empty();
        }

        try {
            String fileNameWithoutExtension = filePath.getFileName().toString().replaceFirst("[.][^.]+$", "");
            String fileHash = calculateFileHash(filePath.toString());
            Set<String> uniqueWordsInFile = new HashSet<>(extractWordsFromFile(filePath.toString()));

            List<String> fileLines = Files.readAllLines(filePath);

            if (fileLines.size() < 3) {
                return Optional.empty();
            }

            String firstLine = fileLines.get(0);
            String firstLinePatternString = "\\[\\[([A-Z]+)_MOC\\]\\]";
            Pattern pattern = Pattern.compile(firstLinePatternString);
            Matcher matcher = pattern.matcher(firstLine);
            String category = matcher.find() ? matcher.group(1) : null;

            String secondLine = fileLines.get(1);
            String prioritySymbol = secondLine.replaceAll("[^\\p{So}]", "");
            Priority priority = mapPrioritySymbol(prioritySymbol);

            return Optional.of(new Note(fileNameWithoutExtension, fileHash, uniqueWordsInFile, category, priority));
        } catch (IOException ioe) {
            log.warn("File ({}) reading error", filePath, ioe);
            return Optional.empty();
        }
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

    /**
     * Maps a priority symbol to a corresponding Priority enum value.
     *
     * @param prioritySymbol The priority symbol to be mapped.
     * @return The Priority enum value associated with the given symbol, or null if no match is found.
     */
    private Priority mapPrioritySymbol(String prioritySymbol) {
        return switch (prioritySymbol) {
            case "✅" -> Priority.COMPLETED;
            case "🔝" -> Priority.HIGH;
            case "🛠️" -> Priority.MEDIUM;
            case "⏳" -> Priority.LOW;
            default -> null;
        };
    }

    /**
     * Extracts words from a file specified by the given file path.
     *
     * @param filePath The path to the file to extract words from.
     * @return A list of words extracted from the file. If the file is empty or cannot be
     * read, an empty list is returned.
     */
    public List<String> extractWordsFromFile(String filePath) {
        Optional<String> optionalFileContent = readContentFile(filePath);

        if (optionalFileContent.isEmpty()) {
            return List.of();
        }

        String fileContent = optionalFileContent.get();
        if (fileContent.isEmpty()) {
            return List.of();
        }

        return Stream.of(fileContent.split("[[#,.\\[\\]/()`!\\\"$%*}{\\\\_':;=?\\-<>+|\\r\\n\\s][0-9]]"))
                .filter(word -> !word.isEmpty())
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
    Optional<String> readContentFile(String filePath) {

        Path path = Path.of(filePath);

        // Check if the file exists and is not a directory
        if (!Files.isRegularFile(path)) {
            log.warn("The file ({}) does not exist or is a directory", filePath);
        }

        try {
            return Optional.of(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException ioe) {
            log.warn("File ({}) reading error", filePath, ioe);
            return Optional.empty();
        }
    }
}
