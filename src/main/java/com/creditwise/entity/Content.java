package com.creditwise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "contents", indexes = {
    @Index(name = "idx_content_status", columnList = "status"),
    @Index(name = "idx_content_category", columnList = "category"),
    @Index(name = "idx_content_creator", columnList = "created_by_user_id")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Content extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(name = "content_type", nullable = false)
    private String contentType = "ARTICLE";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ContentCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_status", nullable = false)
    private ContentStatus contentStatus = ContentStatus.ACTIVE;
    
    public ContentStatus getContentStatus() {
        return contentStatus;
    }
    
    public void setContentStatus(ContentStatus contentStatus) {
        this.contentStatus = contentStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    @JsonBackReference
    private User contentCreator;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ContentLike> likes;

    public enum ContentCategory {
        CREDIT_TIPS,
        FINANCIAL_EDUCATION,
        DEBT_MANAGEMENT,
        CREDIT_MONITORING
    }

    public enum ContentStatus {
        ACTIVE,
        INACTIVE,
        DELETED
    }
}