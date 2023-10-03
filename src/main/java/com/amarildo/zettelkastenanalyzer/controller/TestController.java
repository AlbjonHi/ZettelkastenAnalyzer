package com.amarildo.zettelkastenanalyzer.controller;

import com.amarildo.zettelkastenanalyzer.model.Note;
import com.amarildo.zettelkastenanalyzer.service.NoteFinder;
import com.amarildo.zettelkastenanalyzer.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author aaliaj
 */
@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    NoteService noteService;

    NoteFinder noteFinder;

    @Autowired
    public TestController(NoteService noteService, NoteFinder noteFinder) {
        this.noteService = noteService;
        this.noteFinder = noteFinder;
    }

    @GetMapping(path = "/")
    public String showForm(Model model) {
        noteService.loadDatabase();
        return "index";
    }

    /**
     * React
     */
    @GetMapping(path = "/search")
    public ResponseEntity<List<Map.Entry<String, Long>>> ciao(@RequestParam String words) {

        String[] parole = words.split(" ");

        List<Map.Entry<String, Long>> ciao = noteFinder.getNotesNameAndOccurrences(List.of(parole));

        int toIndex = Math.min(ciao.size(), 50); // restituisco massimo 50 elementi
        return ResponseEntity.ok(ciao.subList(0, toIndex));
    }

    /**
     * Thymeleaf
     */
    @PostMapping("/search")
    public String search(@ModelAttribute("userInput") String wordsss, BindingResult bindingResult, Model model) {

        String[] words = wordsss.split(" ");
        List<Map.Entry<Note, Long>> notesByWordsWithCountsSorted = noteFinder.findNotesByWordsWithCountsSorted(List.of(words));

        model.addAttribute("MAPPA", notesByWordsWithCountsSorted);

        return "a"; // Sostituisci con il nome della vista dei risultati
    }

}
