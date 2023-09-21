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
    private String name;
    private int words;
    private int size;
}
