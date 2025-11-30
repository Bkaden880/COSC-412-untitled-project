package com.syllabus.copilot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyPlan {
    private String overallStrategy;
    private List<StudySession> studySessions;
    private List<String> recommendedResources;
    private String difficultyAssessment;
    private Integer estimatedStudyHours;
    private LocalDateTime generatedAt;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class StudySession {
    private String topic;
    private LocalDateTime scheduledDate;
    private Integer durationMinutes;
    private String studyMethod;
    private String materials;
}