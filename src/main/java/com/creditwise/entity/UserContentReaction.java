package com.creditwise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_content_reactions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"content_id", "user_id"})
}, indexes = {
    @Index(name = "idx_reaction_content", columnList = "content_id"),
    @Index(name = "idx_reaction_user", columnList = "user_id"),
    @Index(name = "idx_reaction_type", columnList = "reaction_type")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserContentReaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    public enum ReactionType {
        LIKE,
        DISLIKE
    }
}