package org.example.commonjpa.repository;

import org.example.commonjpa.entity.ImageDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageDraftRepository extends JpaRepository<ImageDraft, Long> {
    Optional<ImageDraft> findByImage(String image);
}
