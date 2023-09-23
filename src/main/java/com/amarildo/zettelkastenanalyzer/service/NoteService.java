package com.amarildo.zettelkastenanalyzer.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NoteService {

    // TODO (21/09/2023): method for parsing a folder hierarchy recursively file by file

    // TODO (21/09/2023): method to extract all the words contained in a file

    // TODO (22/09/2023): creating a story about the progress of the zettelkasten. i want to display graphs that show the trend over time based on the chosen filters

    // TODO (22/09/2023): find a way to distinguish completed notes from uncompleted ones

    // TODO (23/09/2023): method get note category, priority and notesAddedToAnki from the first lines of the note
    // a generic file starts like this:
    // line 1. -> [[CATEGORY_MOC]]
    // line 2. -> [[Priority = ✅]] (✅ = COMPLETED, 🔝 = HIGH, 🛠️ = MEDIUM, ⏳ = LOW)
    // line 3. -> [[Anki = ⛔]]
    // not all notes have this format. manage the eventuality
    
    public static List<String> extractWordsFromFile(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        FileReader fileReader = new FileReader(filePath);
        Scanner scan = new Scanner(fileReader);
        while (scan.hasNext()) {
            words.add(scan.next());
        }
        
        ArrayList<String> finalWords = new ArrayList<>();
        
        scan.close();
        for (String s : words) {
            if (s.contains("'")) {
                finalWords.add(s.replace("'", ""));
            } else finalWords.add(s);
        }
        
        /*Creating a copied file from native one
        
        File nativeFile = new File("C:/Users/a-sd9/Code/nativeFiles/");
        */
        
        return finalWords;
        // TODO (21/09/2023): method to extract all the words contained in a file
    }
    
    public boolean isFileChanged(File f) {
        // TODO (21/09/2023): method for analyzing whether the file has changed in some way
        return false;
    }
}
