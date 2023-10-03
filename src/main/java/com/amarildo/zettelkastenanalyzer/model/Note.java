package com.amarildo.zettelkastenanalyzer.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "note")
public class Note {

    @Id
    private String fileName;

    private String hash;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn
    private Set<String> words;

    private String category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

}
