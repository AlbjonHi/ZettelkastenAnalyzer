package com.amarildo.zettelkastenanalyzer;


import com.amarildo.zettelkastenanalyzer.model.Note;
import com.amarildo.zettelkastenanalyzer.repository.NoteRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NoteDataGenerator implements CommandLineRunner {

    NoteRepository noteRepository;

    @Autowired
    public NoteDataGenerator(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();

        int numberOfRandomNotes = 100;
        for (int i = 0; i < numberOfRandomNotes; i++) {
            String title = faker.book().title();
            int words = faker.number().numberBetween(100, 5000);
            int size = faker.number().numberBetween(5000, 10000);
            noteRepository.save(new Note(title, words, size));
        }
    }
}
