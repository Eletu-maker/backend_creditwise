package com.creditwise.repository;

import com.creditwise.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
    Page<Content> findByCategory(Content.ContentCategory category, Pageable pageable);
    Page<Content> findByContentType(Content.ContentType contentType, Pageable pageable);
}