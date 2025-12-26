package com.creditwise.repository;

import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);
    
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
            UUID senderId, UUID receiverId, UUID receiverId2, UUID senderId2);
    
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId OR m.receiver.id = :userId) AND m.conversationId = :conversationId")
    List<Message> findByUserIdAndConversationId(UUID userId, String conversationId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByReceiverId(UUID userId);
    
    long countByReceiverIdAndIsReadFalse(UUID receiverId);
}