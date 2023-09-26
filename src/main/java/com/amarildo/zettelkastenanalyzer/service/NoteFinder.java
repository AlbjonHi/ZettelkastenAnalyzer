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


    // Cerca entità Note per un elenco di parole

    /**
     * Finds notes based on a list of words to search for, including counts of occurrences, and sorts them in descending
     * order.
     *
     * @param wordsToSearch A list of words to search for.
     * @return A list of map entries, where each entry contains a note and its count of occurrences, sorted in
     * descending order of counts.
     */
    public List<Map.Entry<Note, Long>> findNotesByWordsWithCountsSorted(List<String> wordsToSearch) {
        return wordToNotesMap.entrySet().stream()
                .filter(entry -> wordsToSearch.contains(entry.getKey())) // Filtra solo le parole di interesse.
                .flatMap(entry -> entry.getValue().stream().map(note -> new AbstractMap.SimpleEntry<>(note, 1L)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Note, Long>comparingByValue().reversed())
                .toList();
    }

    public void clearMap() {
        wordToNotesMap.clear();
    }

}
