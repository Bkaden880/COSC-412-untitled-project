package com.syllabus.copilot.backend.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class PdfProcessingService {

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!isPdfFile(file)) {
            throw new IllegalArgumentException("File is not a PDF");
        }

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            
            log.info("Successfully extracted text from PDF: {} ({} characters)", 
                    file.getOriginalFilename(), text.length());
            
            return text;
        } catch (IOException e) {
            log.error("Error processing PDF file: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to process PDF file: " + e.getMessage());
        }
    }

    private boolean isPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        return "application/pdf".equals(contentType) || 
               (fileName != null && fileName.toLowerCase().endsWith(".pdf"));
    }

    public boolean isValidPdfSize(MultipartFile file) {
        // Limit PDF files to 10MB
        long maxSize = 10 * 1024 * 1024; // 10MB in bytes
        return file.getSize() <= maxSize;
    }
}