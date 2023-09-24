package com.amarildo.zettelkastenanalyzer.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Note")
public class Note {

    @Id
    private String fileName;

    private String hash;

    @ElementCollection
    @OrderColumn
    private Set<String> words;

    private String category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    // puoi creare qui un metodo setter per hash che crei l'hash partendo da 'content'
}
