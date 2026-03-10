package com.creditwise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
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
    @Index(name = "idx_content_creator", columnList = "created_by_user_id"),
    @Index(name = "idx_content_like_count", columnList = "like_count"),
    @Index(name = "idx_content_dislike_count", columnList = "dislike_count")
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
    @Builder.Default
    private String contentType = "ARTICLE";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ContentCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_status", nullable = false)
    @Builder.Default
    private ContentStatus contentStatus = ContentStatus.ACTIVE;
    
    @Column(name = "like_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    @Builder.Default
    private Integer likeCount = 0;
    
    @Column(name = "dislike_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    @Builder.Default
    private Integer dislikeCount = 0;
    
    public ContentStatus getContentStatus() {
        return contentStatus;
    }
    
    public void setContentStatus(ContentStatus contentStatus) {
        this.contentStatus = contentStatus;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getDislikeCount() {
        return dislikeCount;
    }
    
    public void setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
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