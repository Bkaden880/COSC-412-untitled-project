package com.syllabus.copilot.backend.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportantDate {
    private String title;
    private String description;
    private LocalDateTime date;
    private String type; // "deadline", "holiday", "exam", "break"
    private boolean isRecurring;
}