package com.amarildo.zettelkastenanalyzer.repository;


import com.amarildo.zettelkastenanalyzer.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {

    Optional<Note> findByFileName(String fileName);
}
