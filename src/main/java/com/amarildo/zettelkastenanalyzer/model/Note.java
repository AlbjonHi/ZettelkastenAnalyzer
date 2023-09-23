package com.amarildo.zettelkastenanalyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Note {

    @Id
    private String title;
    private String text;
    private String category;
    private Priority priority;
    private boolean notesAddedToAnki;
}
