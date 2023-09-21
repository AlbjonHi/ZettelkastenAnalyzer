package com.amarildo.zettelkastenanalyzer.repository;


import com.amarildo.zettelkastenanalyzer.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {

}
