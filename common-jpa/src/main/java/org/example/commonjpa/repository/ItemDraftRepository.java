package org.example.commonjpa.repository;

import org.example.commonjpa.entity.ItemDraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDraftRepository extends JpaRepository<ItemDraft, Long> {
    ItemDraft findBySessionId(Long sessionId);
}
