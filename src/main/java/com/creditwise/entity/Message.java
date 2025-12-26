package com.creditwise.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_message_sender", columnList = "sender_id"),
        @Index(name = "idx_message_receiver", columnList = "receiver_id"),
        @Index(name = "idx_message_conversation", columnList = "conversation_id")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "message_text", nullable = false, length = 2000)
    private String messageText;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "message_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }
    
    public enum MessageType {
        TEXT,
        FILE,
        SYSTEM
    }
}