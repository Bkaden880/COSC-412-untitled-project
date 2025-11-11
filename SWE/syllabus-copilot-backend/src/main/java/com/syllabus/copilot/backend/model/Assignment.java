package com.syllabus.copilot.backend.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer points;
    private String type; // e.g., "homework", "project", "quiz"
    private String instructions;
    private boolean isGroupWork;
}