package com.amarildo.zettelkastenanalyzer.service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NoteService {
    
    // TODO (21/09/2023): method for parsing a folder hierarchy recursively file by file
    
    
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
    
    public boolean isFileChanged(String filePath) {
        return false;
        // TODO (21/09/2023): method for analyzing whether the file has changed in some way
    }
}
