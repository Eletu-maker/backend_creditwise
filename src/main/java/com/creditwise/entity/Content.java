package com.creditwise.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "contents", indexes = {
    @Index(name = "idx_content_category", columnList = "category"),
    @Index(name = "idx_content_created_by", columnList = "created_by_user_id")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Content extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User contentCreator;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ContentCategory category;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    public enum ContentType {
        ARTICLE,
        VIDEO,
        INFOGRAPHIC,
        PODCAST
    }

    public enum ContentCategory {
        PAYMENT_HISTORY,
        CREDIT_UTILIZATION,
        CREDIT_AGE,
        CREDIT_MIX,
        RECENT_ACTIVITY,
        GENERAL_EDUCATION
    }
}