package com.creditwise.repository;

import com.creditwise.entity.ContentLike;
import com.creditwise.entity.Content;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentLikeRepository extends JpaRepository<ContentLike, UUID> {
    Optional<ContentLike> findByContentAndUser(Content content, User user);
    boolean existsByContentAndUser(Content content, User user);
    void deleteByContentAndUser(Content content, User user);
}