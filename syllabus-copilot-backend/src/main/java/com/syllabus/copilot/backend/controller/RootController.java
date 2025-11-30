package com.syllabus.copilot.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Syllabus Copilot Backend API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "test", "GET /api/syllabi/test",
            "upload", "POST /api/syllabi/upload",
            "getUserSyllabi", "GET /api/syllabi/user/{userId}",
            "getSyllabus", "GET /api/syllabi/{id}",
            "getByStatus", "GET /api/syllabi/status/{status}",
            "delete", "DELETE /api/syllabi/{id}"
        ));
        return ResponseEntity.ok(response);
    }
}
