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
        
        // Simple pattern matching for assignments
        Pattern assignmentPattern = Pattern.compile("(?i)(assignment|homework|project|lab)\\s*(\\d+)?:?\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = assignmentPattern.matcher(text);
        
        while (matcher.find()) {
            Assignment assignment = new Assignment();
            assignment.setTitle(matcher.group(0).trim());
            assignment.setType(matcher.group(1).toLowerCase());
            assignment.setDescription(matcher.group(3));
            assignments.add(assignment);
        }
        
        syllabus.setAssignments(assignments);
    }

    private void extractExams(Syllabus syllabus, String text) {
        List<Exam> exams = new ArrayList<>();
        
        Pattern examPattern = Pattern.compile("(?i)(midterm|final|exam|quiz)\\s*(?:exam)?:?\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = examPattern.matcher(text);
        
        while (matcher.find()) {
            Exam exam = new Exam();
            exam.setTitle(matcher.group(0).trim());
            exam.setType(matcher.group(1).toLowerCase());
            exam.setCoverageTopics(matcher.group(2));
            exams.add(exam);
        }
        
        syllabus.setExams(exams);
    }

    private void extractImportantDates(Syllabus syllabus, String text) {
        List<ImportantDate> dates = new ArrayList<>();
        
        // Look for date patterns
        Pattern datePattern = Pattern.compile("(?i)(\\d{1,2}[/\\-]\\d{1,2}[/\\-]\\d{2,4})\\s*[:-]?\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = datePattern.matcher(text);
        
        while (matcher.find()) {
            ImportantDate date = new ImportantDate();
            date.setTitle(matcher.group(2).trim());
            date.setDescription(matcher.group(0));
            date.setType("deadline");
            dates.add(date);
        }
        
        syllabus.setImportantDates(dates);
    }

    private void generateAISummary(Syllabus syllabus, String text) {
        // For now, generate a simple summary based on the extracted text
        // In a real implementation, this would call OpenAI API
        
        StringBuilder summary = new StringBuilder();
        summary.append("Course: ").append(syllabus.getCourseName()).append("\\n");
        
        if (syllabus.getCourseCode() != null) {
            summary.append("Code: ").append(syllabus.getCourseCode()).append("\\n");
        }
        
        if (syllabus.getInstructor() != null) {
            summary.append("Instructor: ").append(syllabus.getInstructor()).append("\\n");
        }
        
        summary.append("Text Length: ").append(text.length()).append(" characters\\n");
        summary.append("Assignments: ").append(syllabus.getAssignments().size()).append("\\n");
        summary.append("Exams: ").append(syllabus.getExams().size()).append("\\n");
        
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