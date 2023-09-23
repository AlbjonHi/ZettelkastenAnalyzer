package com.amarildo.zettelkastenanalyzer;


import com.amarildo.zettelkastenanalyzer.model.Note;
import com.amarildo.zettelkastenanalyzer.model.Priority;
import com.amarildo.zettelkastenanalyzer.repository.NoteRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NoteDataGenerator implements CommandLineRunner {

    private static final int NUMBER_OF_RANDOM_NOTES = 100;
    private static final int WORDS_PER_NOTE = 10;

    private final Random random = new Random();
    private final NoteRepository noteRepository;

    @Autowired
    public NoteDataGenerator(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void run(String... args) {
        Faker faker = new Faker();

        for (int i = 0; i < NUMBER_OF_RANDOM_NOTES; i++) {

            String title = faker.book().title();

            String text = String.join(" ", faker.lorem().words(WORDS_PER_NOTE));

            String category = "Java";

            Priority[] priorities = Priority.values();
            Priority priority = priorities[random.nextInt(priorities.length)];

            boolean addedToAnki = faker.bool().bool();

            noteRepository.save(new Note(title, text, category, priority, addedToAnki));
        }
    }
}
