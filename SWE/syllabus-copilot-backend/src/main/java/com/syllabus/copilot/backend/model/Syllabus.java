package com.syllabus.copilot.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "syllabi")
public class Syllabus {
    @Id
    private String id;
    private String userId;
    private String courseName;
    private String courseCode;
    private String instructor;
    private String semester;
    private String fileName;
    private String originalText;
    private LocalDateTime uploadDate;
    
    // Course details
    private String description;
    private List<String> learningObjectives;
    private String gradingPolicy;
    
    // Assignments and assessments
    private List<Assignment> assignments;
    private List<Exam> exams;
    
    // Important dates
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ImportantDate> importantDates;
    
    // AI-generated content
    private String aiGeneratedSummary;
    private StudyPlan studyPlan;
    
    // File processing status
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;
    private String processingError;
}
