package com.syllabus.copilot.backend.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exam {
    private String title;
    private String type; // "midterm", "final", "quiz"
    private LocalDateTime dateTime;
    private String location;
    private Integer duration; // in minutes
    private Integer points;
    private String format; // "online", "in-person", "take-home"
    private String coverageTopics;
}