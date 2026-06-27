package com.proofit.proofit.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "document_hash", nullable = false, length = 64)
    private String documentHash;

    @Column(name = "chain_hash", nullable = false, length = 64)
    private String chainHash;

    @Column(name = "previous_chain_hash", length = 64)
    private String previousChainHash;

    @Column(name = "verification_token", unique = true)
    private String verificationToken;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
}