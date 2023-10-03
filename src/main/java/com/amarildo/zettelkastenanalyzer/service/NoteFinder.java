package com.amarildo.zettelkastenanalyzer.service;

import com.amarildo.zettelkastenanalyzer.model.Note;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author aaliaj
 */
@Service
public class NoteFinder {

    private final Map<String, Set<Note>> wordToNotesMap = new HashMap<>();

    /**
     * Adds a note to the collection of notes, associating it with the words it contains.
     * <p>
     * This method takes a Note object and associates it with the individual words it contains in the wordToNotesMap.
     * If a word is not already present in the map, a new HashSet is created for that word. The note is then added to
     * the corresponding HashSet.
     *
     * @param note The Note object to be added to the collection.
     */
    public void addNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }
        note.getWords().forEach(word -> wordToNotesMap
                .computeIfAbsent(word, k -> new HashSet<>())
                .add(note));
    }

    public List<Map.Entry<String, Long>> getNotesNameAndOccurrences(List<String> wordstoSearch) {
        List<Map.Entry<Note, Long>> notesByWordsWithCountsSorted = findNotesByWordsWithCountsSorted(wordstoSearch);

        return notesByWordsWithCountsSorted.stream()
                .map(entry -> {
                    String fileName = entry.getKey().getFileName();
                    Long occorrenze = entry.getValue();
                    return Map.entry(fileName, occorrenze);
                })
                .collect(Collectors.toList());
    }

    /**
     * Find notes, check how often certain words appear in the names of note files and inside the notes themselves, and
     * give back a list that shows the notes along with how many times those words were found. The list is arranged so
     * that notes with the most occurrences come first.
     *
     * @param wordsToSearch A list of words to search for in the names of note files.
     * @return A list of entries where each entry contains a note and the count of occurrences of the searched words
     * in the names of note files, sorted in descending order of count.
     */
    public List<Map.Entry<Note, Long>> findNotesByWordsWithCountsSorted(List<String> wordsToSearch) {
        return wordToNotesMap.entrySet().stream()
                .filter(entry -> wordsToSearch.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream()
                        .map(note -> {

                            long occ = 1L;

                            String nomeFile = note.getFileName().toLowerCase();
                            for (String toSearch : wordsToSearch) {
                                if (nomeFile.contains(toSearch.toLowerCase())) { // se il nome contiene una delle parole cercate -> +1
                                    occ++;
                                }
                            }

                            return new AbstractMap.SimpleEntry<>(note, occ);
                        }))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingLong(Map.Entry::getValue)))
                .entrySet().stream()
                .sorted(Map.Entry.<Note, Long>comparingByValue().reversed())
                .toList();
    }

    public void clearMap() {
        wordToNotesMap.clear();
    }

}
