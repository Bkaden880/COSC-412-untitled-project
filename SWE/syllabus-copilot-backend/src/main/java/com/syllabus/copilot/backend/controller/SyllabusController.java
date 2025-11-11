package com.syllabus.copilot.backend.controller;

import com.syllabus.copilot.backend.model.ProcessingStatus;
import com.syllabus.copilot.backend.model.Syllabus;
import com.syllabus.copilot.backend.service.SyllabusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/syllabi")
@CrossOrigin(origins = "*")
public class SyllabusController {
    @Autowired
    private SyllabusService service;

    // Get syllabi for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Syllabus>> getSyllabi(@PathVariable @NotBlank String userId) {
        try {
            List<Syllabus> syllabi = service.getAllForUser(userId);
            return ResponseEntity.ok(syllabi);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a specific syllabus by ID
    @GetMapping("/{id}")
    public ResponseEntity<Syllabus> getSyllabusById(@PathVariable String id) {
        Optional<Syllabus> syllabus = service.findById(id);
        return syllabus.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Upload and process syllabus file
    @PostMapping("/upload")
    public ResponseEntity<?> uploadSyllabusFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") @NotBlank String userId,
            @RequestParam("courseName") @NotBlank String courseName) {
        
        try {
            // Validate inputs
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("File cannot be empty"));
            }

            if (userId.trim().isEmpty() || courseName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("UserId and courseName are required"));
            }

            // Process the syllabus file
            Syllabus syllabus = service.processSyllabusFile(file, userId, courseName);
            
            return ResponseEntity.ok(syllabus);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error processing file: " + e.getMessage()));
        }
    }

    // Get syllabi by processing status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Syllabus>> getSyllabiByStatus(@PathVariable ProcessingStatus status) {
        try {
            List<Syllabus> syllabi = service.findByProcessingStatus(status);
            return ResponseEntity.ok(syllabi);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete syllabus
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSyllabus(@PathVariable String id) {
        try {
            Optional<Syllabus> syllabus = service.findById(id);
            if (syllabus.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error deleting syllabus: " + e.getMessage()));
        }
    }

    // Simple test route
    @GetMapping("/test")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("✅ Enhanced Backend is running perfectly with PDF processing!");
    }

    // Error response class
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
