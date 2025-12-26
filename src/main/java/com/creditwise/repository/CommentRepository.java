package com.creditwise.repository;

import com.creditwise.entity.Comment;
import com.creditwise.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByContentOrderByCreatedAtDesc(Content content);
}