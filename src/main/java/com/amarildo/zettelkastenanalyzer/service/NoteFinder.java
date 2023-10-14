package com.amarildo.zettelkastenanalyzer.service;

import com.amarildo.zettelkastenanalyzer.model.Note;
import org.springframework.stereotype.Service;

import java.util.*;
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
        List<Map.Entry<Note, Long>> notesByWordsWithCountsSorted = findNotesByWordsWithCountSorted(wordstoSearch);

        return notesByWordsWithCountsSorted.stream()
                .map(entry -> {
                    String fileName = entry.getKey().getFileName();
                    Long occorrenze = entry.getValue();
                    return Map.entry(fileName, occorrenze);
                })
                .collect(Collectors.toList());
    }


    public List<Map.Entry<Note, Long>> findNotesByWordsWithCountSorted(List<String> wordsToSearch) {
        
        List<Map.Entry<Note, Long>> ret = new ArrayList<>();
        
        for (String toSearch : wordsToSearch) {
            if (wordToNotesMap.containsKey(toSearch)) {
                Set<Note> notes = wordToNotesMap.get(toSearch);
                for (Note note : notes) {
                    String fileName = note.getFileName();
                    Set<String> words = note.getWords();
                    for (String word : words) {
                        if (word.equals(toSearch)) {
                            ret.add(new AbstractMap.SimpleEntry<>(note, 1L));
                        }
                    }
                    if (fileName.toLowerCase().contains(toSearch)) {
                        ret.add(new AbstractMap.SimpleEntry<>(note, 1L));
                    }
                }
            }
        }
        
        Map<Note, Long> collect = ret.stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingLong(Map.Entry::getValue)));
        return collect.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();
    }
    
    public void clearMap() {
        wordToNotesMap.clear();
    }

}
