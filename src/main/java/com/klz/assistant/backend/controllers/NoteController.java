package com.klz.assistant.backend.controllers;

import com.klz.assistant.backend.exceptions.ResourceNotFoundException;
import com.klz.assistant.backend.models.Note;
import com.klz.assistant.backend.repository.NoteRepository;
import com.klz.assistant.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class NoteController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping("/users/{userId}/notes")
    public List<Note> getNotesByUserId(@PathVariable Long userId) {
        return noteRepository.findByUserId(userId);
    }

    @PostMapping("/users/{userId}/notes")
    public Note addNote(@PathVariable Long userId,
                            @Valid @RequestBody Note note) {
        return userRepository.findById(userId)
                .map(user -> {
                    note.setUser(user);
                    return noteRepository.save(note);
                }).orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + userId));
    }

    @PutMapping("/users/{userId}/notes/{noteId}")
    public Note updateNote(@PathVariable Long userId,
                           @PathVariable Long noteId,
                           @Valid @RequestBody Note noteRequest) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        return noteRepository.findById(noteId)
                .map(note -> {
                    note.setTitle(noteRequest.getTitle());
                    note.setDescription(noteRequest.getDescription());
                    return noteRepository.save(note);
                }).orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + noteId));
    }

    @DeleteMapping("/users/{userId}/notes/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long userId,
                                        @PathVariable Long noteId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        return noteRepository.findById(noteId)
                .map(note -> {
                    noteRepository.delete(note);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + noteId));

    }
}
