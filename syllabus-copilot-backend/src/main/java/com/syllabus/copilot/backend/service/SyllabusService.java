package com.syllabus.copilot.backend.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.syllabus.copilot.backend.model.ProcessingStatus;
import com.syllabus.copilot.backend.model.Syllabus;
import com.syllabus.copilot.backend.repository.SyllabusRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SyllabusService {
    private final SyllabusRepository repo;
    private final PdfProcessingService pdfProcessingService;
    private final SyllabusAnalysisService analysisService;

    public SyllabusService(SyllabusRepository repo, 
                          PdfProcessingService pdfProcessingService,
                          SyllabusAnalysisService analysisService) {
        this.repo = repo;
        this.pdfProcessingService = pdfProcessingService;
        this.analysisService = analysisService;
    }

    public List<Syllabus> getAllForUser(String userId) {
        return repo.findByUserId(userId);
    }

    public List<Syllabus> getAll() {
        return repo.findAll();
    }

    public Optional<Syllabus> findById(String id) {
        return repo.findById(id);
    }

    public Syllabus processSyllabusFile(MultipartFile file, String userId, String courseName) {
        log.info("Processing syllabus file: {} for user: {}", file.getOriginalFilename(), userId);
        
        // Validate file
        validateFile(file);
        
        // Create initial syllabus record
        Syllabus syllabus = new Syllabus();
        syllabus.setUserId(userId);
        syllabus.setCourseName(courseName);
        syllabus.setFileName(file.getOriginalFilename());
        syllabus.setUploadDate(LocalDateTime.now());
        syllabus.setProcessingStatus(ProcessingStatus.PROCESSING);
        
        // Save initial record
        syllabus = repo.save(syllabus);
        
        try {
            // Extract text from PDF
            String extractedText = pdfProcessingService.extractTextFromPdf(file);
            
            // Analyze syllabus content
            analysisService.analyzeSyllabusContent(syllabus, extractedText);
            
            // Save updated syllabus
            return repo.save(syllabus);
            
        } catch (IOException e) {
            log.error("Error processing syllabus file", e);
            syllabus.setProcessingStatus(ProcessingStatus.FAILED);
            syllabus.setProcessingError("Failed to extract text from PDF: " + e.getMessage());
            return repo.save(syllabus);
        } catch (Exception e) {
            log.error("Unexpected error during syllabus processing", e);
            syllabus.setProcessingStatus(ProcessingStatus.FAILED);
            syllabus.setProcessingError("Unexpected error: " + e.getMessage());
            return repo.save(syllabus);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        if (!pdfProcessingService.isValidPdfSize(file)) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
        
        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }
    }

    public Syllabus save(Syllabus syllabus) {
        if (syllabus.getUploadDate() == null) {
            syllabus.setUploadDate(LocalDateTime.now());
        }
        return repo.save(syllabus);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
    
    public List<Syllabus> findByProcessingStatus(ProcessingStatus status) {
        return repo.findByProcessingStatus(status);
    }
}
