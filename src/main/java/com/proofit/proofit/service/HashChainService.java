package com.proofit.proofit.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.util.List;
import com.proofit.proofit.model.Document;

@Service
public class HashChainService {

    public String generateDocumentHash(byte[] fileBytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        return bytesToHex(hashBytes);
    }

    public String generateChainHash(String documentHash,
                                    String timestamp,
                                    String previousChainHash) throws Exception {
        String combined = documentHash + timestamp +
                (previousChainHash != null ? previousChainHash : "GENESIS");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(combined.getBytes());
        return bytesToHex(hashBytes);
    }

    public boolean verifyDocument(byte[] fileBytes,
                                  String storedHash) throws Exception {
        String currentHash = generateDocumentHash(fileBytes);
        return currentHash.equals(storedHash);
    }

    public boolean verifyChainIntegrity(List<Document> documents) throws Exception {
        for (int i = 1; i < documents.size(); i++) {
            Document current = documents.get(i);
            Document previous = documents.get(i - 1);
            String recomputed = generateChainHash(
                    current.getDocumentHash(),
                    current.getUploadedAt().toString(),
                    previous.getChainHash()
            );
            if (!recomputed.equals(current.getChainHash())) {
                return false;
            }
        }
        return true;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}