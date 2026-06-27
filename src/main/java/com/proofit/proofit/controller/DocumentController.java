package com.proofit.proofit.controller;

import com.proofit.proofit.model.Document;
import com.proofit.proofit.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file,
                                           Authentication auth) throws Exception {
        Document doc = documentService.uploadDocument(file, auth.getName());
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Document>> myDocuments(Authentication auth) {
        return ResponseEntity.ok(documentService.getUserDocuments(auth.getName()));
    }

    @GetMapping("/public/verify/{token}")
    public ResponseEntity<Map<String, Object>> verifyPublic(@PathVariable String token) {
        Document doc = documentService.getByToken(token);
        return ResponseEntity.ok(Map.of(
                "fileName", doc.getFileName(),
                "uploadedAt", doc.getUploadedAt().toString(),
                "documentHash", doc.getDocumentHash(),
                "chainHash", doc.getChainHash(),
                "verified", true,
                "message", "✅ This document was provably created on " + doc.getUploadedAt()
        ));
    }

    @PostMapping("/verify/{token}/file")
    public ResponseEntity<Map<String, Object>> verifyFile(
            @PathVariable String token,
            @RequestParam("file") MultipartFile file) throws Exception {
        boolean match = documentService.verifyFile(token, file.getBytes());
        return ResponseEntity.ok(Map.of(
                "verified", match,
                "message", match
                        ? "✅ File matches. Original. Not tampered."
                        : "❌ File does not match. Possibly tampered."
        ));
    }
}