package com.syllabus.copilot.backend.service;

import org.springframework.stereotype.Service;

import com.syllabus.copilot.backend.model.*;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SyllabusAnalysisService {

    public void analyzeSyllabusContent(Syllabus syllabus, String extractedText) {
        log.info("Starting syllabus analysis for course: {}", syllabus.getCourseName());
        
        syllabus.setOriginalText(extractedText);
        
        // Extract course information
        extractCourseInfo(syllabus, extractedText);
        
        // Extract assignments
        extractAssignments(syllabus, extractedText);
        
        // Extract exams
        extractExams(syllabus, extractedText);
        
        // Extract important dates
        extractImportantDates(syllabus, extractedText);
        
        // Generate AI summary (placeholder for now)
        generateAISummary(syllabus, extractedText);
        
        syllabus.setProcessingStatus(ProcessingStatus.COMPLETED);
        log.info("Completed syllabus analysis for course: {}", syllabus.getCourseName());
    }

    private void extractCourseInfo(Syllabus syllabus, String text) {
        // Extract course code
        Pattern courseCodePattern = Pattern.compile("(?i)course\\s+(?:code|number)?:?\\s*([A-Z]{2,4}\\s*\\d{3,4})", Pattern.CASE_INSENSITIVE);
        Matcher courseCodeMatcher = courseCodePattern.matcher(text);
        if (courseCodeMatcher.find()) {
            syllabus.setCourseCode(courseCodeMatcher.group(1).trim());
        }

        // Extract instructor
        Pattern instructorPattern = Pattern.compile("(?i)instructor:?\\s*([A-Za-z\\s,\\.]+?)(?:\\n|$|professor|dr\\.|email)", Pattern.CASE_INSENSITIVE);
        Matcher instructorMatcher = instructorPattern.matcher(text);
        if (instructorMatcher.find()) {
            syllabus.setInstructor(instructorMatcher.group(1).trim());
        }

        // Extract semester/term
        Pattern semesterPattern = Pattern.compile("(?i)(fall|spring|summer|winter)\\s*(\\d{4})", Pattern.CASE_INSENSITIVE);
        Matcher semesterMatcher = semesterPattern.matcher(text);
        if (semesterMatcher.find()) {
            syllabus.setSemester(semesterMatcher.group(0));
        }

        // Extract course description
        Pattern descPattern = Pattern.compile("(?i)(?:course\\s+)?description:?\\s*([^\\n]{50,500})", Pattern.CASE_INSENSITIVE);
        Matcher descMatcher = descPattern.matcher(text);
        if (descMatcher.find()) {
            syllabus.setDescription(descMatcher.group(1).trim());
        }
    }

    private void extractAssignments(Syllabus syllabus, String text) {
        List<Assignment> assignments = new ArrayList<>();
        
        // Pattern to capture: "Homework 1 — Due 09/10/2025" or "Quiz 1 — 09/15/2025" format
        // Must have: type + number + separator (— or -) + optional "Due" + valid date
        Pattern assignmentPattern = Pattern.compile(
            "(?i)(homework|assignment|project|lab|quiz)\\s+(\\d+)\\s*[—\\-]\\s*(?:due\\s+)?(\\d{2}/\\d{2}/\\d{4})",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = assignmentPattern.matcher(text);
        
        while (matcher.find()) {
            Assignment assignment = new Assignment();
            String type = matcher.group(1);
            String number = matcher.group(2);
            String dateStr = matcher.group(3);
            
            assignment.setTitle(type + " " + number);
            assignment.setType(type.toLowerCase());
            assignment.setDescription(type + " " + number + " - Due " + dateStr);
            
            try {
                assignment.setDueDate(parseDateString(dateStr));
            } catch (Exception e) {
                log.warn("Failed to parse assignment date: {}", dateStr);
            }
            
            assignments.add(assignment);
        }
        
        syllabus.setAssignments(assignments);
    }

    private void extractExams(Syllabus syllabus, String text) {
        List<Exam> exams = new ArrayList<>();
        
        // Pattern to capture: "Midterm Exam — 10/20/2025"
        // Must have: type + "Exam" + separator (— or -) + valid date
        Pattern examPattern = Pattern.compile(
            "(?i)(midterm|final)\\s+exam\\s*[—\\-]\\s*(\\d{2}/\\d{2}/\\d{4})",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = examPattern.matcher(text);
        
        while (matcher.find()) {
            Exam exam = new Exam();
            String type = matcher.group(1);
            String dateStr = matcher.group(2);
            
            exam.setTitle(type + " Exam");
            exam.setType(type.toLowerCase());
            exam.setCoverageTopics("See syllabus for details");
            
            try {
                exam.setDateTime(parseDateString(dateStr));
            } catch (Exception e) {
                log.warn("Failed to parse exam date: {}", dateStr);
            }
            
            exams.add(exam);
        }
        
        syllabus.setExams(exams);
    }

    private void extractImportantDates(Syllabus syllabus, String text) {
        List<ImportantDate> dates = new ArrayList<>();
        
        // Look for date patterns with context
        Pattern datePattern = Pattern.compile(
            "(?i)(\\d{1,2}[/\\-]\\d{1,2}[/\\-]\\d{2,4})\\s*[:-]?\\s*([^\\n]+)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = datePattern.matcher(text);
        
        while (matcher.find()) {
            String dateStr = matcher.group(1);
            String description = matcher.group(2).trim();
            
            // Skip if already captured as assignment or exam
            if (description.toLowerCase().matches(".*(assignment|homework|exam|quiz|midterm|final).*")) {
                continue;
            }
            
            ImportantDate date = new ImportantDate();
            date.setTitle(description.length() > 50 ? description.substring(0, 50) + "..." : description);
            date.setDescription(description);
            date.setType(determineImportantDateType(description));
            
            try {
                date.setDate(parseDateString(dateStr));
            } catch (Exception e) {
                log.warn("Failed to parse important date: {}", dateStr);
            }
            
            dates.add(date);
        }
        
        syllabus.setImportantDates(dates);
    }
    
    private String determineImportantDateType(String description) {
        String lower = description.toLowerCase();
        if (lower.contains("holiday") || lower.contains("no class") || lower.contains("break")) {
            return "holiday";
        } else if (lower.contains("deadline") || lower.contains("due")) {
            return "deadline";
        } else if (lower.contains("drop") || lower.contains("withdraw")) {
            return "administrative";
        }
        return "other";
    }
    
    private LocalDateTime parseDateString(String dateStr) {
        // Parse MM/DD/YYYY or MM-DD-YYYY or YYYY-MM-DD
        String[] parts;
        if (dateStr.contains("/")) {
            parts = dateStr.split("/");
        } else {
            parts = dateStr.split("-");
        }
        
        int year, month, day;
        
        if (parts[0].length() == 4) {
            // YYYY-MM-DD format
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            day = Integer.parseInt(parts[2]);
        } else {
            // MM/DD/YYYY or MM-DD-YYYY format
            month = Integer.parseInt(parts[0]);
            day = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
            
            // Handle 2-digit years
            if (year < 100) {
                year += 2000;
            }
        }
        
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    private void generateAISummary(Syllabus syllabus, String text) {
        // For now, generate a simple summary based on the extracted text
        // In a real implementation, this would call OpenAI API
        
        StringBuilder summary = new StringBuilder();
        summary.append("Course: ").append(syllabus.getCourseName()).append("\n");
        
        if (syllabus.getCourseCode() != null) {
            summary.append("Code: ").append(syllabus.getCourseCode()).append("\n");
        }
        
        if (syllabus.getInstructor() != null) {
            summary.append("Instructor: ").append(syllabus.getInstructor()).append("\n");
        }
        
        summary.append("Text Length: ").append(text.length()).append(" characters\n");
        summary.append("Assignments: ").append(syllabus.getAssignments().size()).append("\n");
        summary.append("Exams: ").append(syllabus.getExams().size()).append("\n");
        
        syllabus.setAiGeneratedSummary(summary.toString());
        
        // Generate basic study plan
        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setOverallStrategy("Regular review and practice");
        studyPlan.setDifficultyAssessment("Moderate");
        studyPlan.setEstimatedStudyHours(syllabus.getAssignments().size() * 5);
        studyPlan.setGeneratedAt(LocalDateTime.now());
        studyPlan.setRecommendedResources(Arrays.asList("Textbook", "Lecture notes", "Online resources"));
        
        syllabus.setStudyPlan(studyPlan);
    }
}