package com.amarildo.zettelkastenanalyzer.controller;

import com.amarildo.zettelkastenanalyzer.model.Note;
import com.amarildo.zettelkastenanalyzer.service.NoteFinder;
import com.amarildo.zettelkastenanalyzer.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        List<Map.Entry<Note, Long>> java = noteFinder.findNotesByWordsWithCountsSorted(
                List.of("morte"));
        model.addAttribute("MAPPA", java);
        return "a";
    }

}
