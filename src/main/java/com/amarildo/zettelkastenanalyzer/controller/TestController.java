package com.amarildo.zettelkastenanalyzer.controller;

import com.amarildo.zettelkastenanalyzer.model.Note;
import com.amarildo.zettelkastenanalyzer.service.NoteFinder;
import com.amarildo.zettelkastenanalyzer.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

/**
 * @author aaliaj
 */
@Controller
public class TestController {

    NoteService noteService;

    NoteFinder noteFinder;

    @Autowired
    public TestController(NoteService noteService, NoteFinder noteFinder) {
        this.noteService = noteService;
        this.noteFinder = noteFinder;
    }

    @GetMapping
    public String showForm(Model model) {
        noteService.loadDatabase();
        return "a";
    }

    // TODO (26/09/2023 - aaliaj): aggiungere nell'interfaccia il numero totale di risultati della ricerca
    // TODO (26/09/2023 - aaliaj): creare una pagina con react che filtra quello presente nella lista con tutto 
    // TODO (26/09/2023 - aaliaj): senza inviare POST per aggioranre l'elenco 

    @PostMapping("/search")
    public String search(@ModelAttribute("userInput") String wordsss, BindingResult bindingResult, Model model) {

        String[] words = wordsss.split(" ");
        List<Map.Entry<Note, Long>> notesByWordsWithCountsSorted = noteFinder.findNotesByWordsWithCountsSorted(List.of(words));

        model.addAttribute("MAPPA", notesByWordsWithCountsSorted);

        return "a"; // Sostituisci con il nome della vista dei risultati
    }

}
