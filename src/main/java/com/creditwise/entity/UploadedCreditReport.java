package com.creditwise.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "uploaded_credit_reports", indexes = {
    @Index(name = "idx_report_client", columnList = "client_id"),
    @Index(name = "idx_report_upload_date", columnList = "upload_date")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedCreditReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_date", nullable = false)
    private java.time.LocalDateTime uploadDate;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "summary", length = 1000)
    private String summary;

    @Column(name = "issues", columnDefinition = "TEXT")
    private String issues;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "is_expired")
    private Boolean isExpired = false;
}