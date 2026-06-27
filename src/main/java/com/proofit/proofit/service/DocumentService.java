package com.proofit.proofit.service;

import com.proofit.proofit.model.Document;
import com.proofit.proofit.model.User;
import com.proofit.proofit.repository.DocumentRepository;
import com.proofit.proofit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private HashChainService hashChainService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    public Document uploadDocument(MultipartFile file, String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        byte[] fileBytes = file.getBytes();
        String timestamp = LocalDateTime.now().toString();
        String documentHash = hashChainService.generateDocumentHash(fileBytes);

        Optional<Document> lastDoc = documentRepository
                .findTopByUserIdOrderByUploadedAtDesc(user.getId());
        String previousChainHash = lastDoc.map(Document::getChainHash).orElse(null);

        String chainHash = hashChainService.generateChainHash(
                documentHash, timestamp, previousChainHash
        );

        Document document = new Document();
        document.setUser(user);
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setDocumentHash(documentHash);
        document.setChainHash(chainHash);
        document.setPreviousChainHash(previousChainHash);
        document.setVerificationToken(UUID.randomUUID().toString());

        return documentRepository.save(document);
    }

    public List<Document> getUserDocuments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByUserIdOrderByUploadedAtDesc(user.getId());
    }

    public boolean verifyFile(String token, byte[] fileBytes) throws Exception {
        Document doc = documentRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return hashChainService.verifyDocument(fileBytes, doc.getDocumentHash());
    }

    public Document getByToken(String token) {
        return documentRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}