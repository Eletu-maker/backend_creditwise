package com.creditwise.repository;

import com.creditwise.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
    Page<Content> findByCategoryAndContentStatusNot(Content.ContentCategory category, Content.ContentStatus statusExcluded, Pageable pageable);
    
    @Query("SELECT c FROM Content c JOIN FETCH c.contentCreator WHERE c.category = :category AND c.contentStatus != :statusExcluded")
    Page<Content> findByCategoryWithCreatorAndContentStatusNot(Content.ContentCategory category, Content.ContentStatus statusExcluded, Pageable pageable);
    
    @Query("SELECT c FROM Content c JOIN FETCH c.contentCreator WHERE c.contentStatus != :statusExcluded")
    Page<Content> findAllWithCreatorAndContentStatusNot(Content.ContentStatus statusExcluded, Pageable pageable);

    @Query("SELECT c FROM Content c JOIN FETCH c.contentCreator WHERE c.id = :contentId")
    Optional<Content> findByIdWithEagerFetch(UUID contentId);
    
    // Additional query to find content by status
    Page<Content> findByContentStatusNot(Content.ContentStatus statusExcluded, Pageable pageable);
}