package com.proofit.proofit.repository;

import com.proofit.proofit.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserIdOrderByUploadedAtDesc(Long userId);
    Optional<Document> findTopByUserIdOrderByUploadedAtDesc(Long userId);
    Optional<Document> findByVerificationToken(String token);
}