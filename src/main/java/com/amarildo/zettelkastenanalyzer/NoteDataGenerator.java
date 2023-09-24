package com.amarildo.zettelkastenanalyzer;


import com.amarildo.zettelkastenanalyzer.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class NoteDataGenerator implements CommandLineRunner {

    private final NoteService noteService;

    @Autowired
    public NoteDataGenerator(NoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    public void run(String... args) throws IOException {

        noteService.fileVisitorOnTree("C:\\Users\\aaliaj\\Documents\\Zettelkasten",
                List.of(
                        ".git",
                        "immagini",
                        ".obsidian"));

    }
}
