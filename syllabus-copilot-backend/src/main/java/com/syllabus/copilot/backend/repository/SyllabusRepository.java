package com.syllabus.copilot.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.syllabus.copilot.backend.model.ProcessingStatus;
import com.syllabus.copilot.backend.model.Syllabus;

public interface SyllabusRepository extends MongoRepository<Syllabus, String> {
    List<Syllabus> findByUserId(String userId);
    List<Syllabus> findByProcessingStatus(ProcessingStatus status);
    List<Syllabus> findByUserIdAndProcessingStatus(String userId, ProcessingStatus status);
}
