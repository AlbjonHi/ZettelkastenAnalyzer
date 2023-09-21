package com.amarildo.zettelkastenanalyzer.model;

import lombok.Data;

@Data
public class Note {

    private String name;
    private int words;
    private int size;
}
