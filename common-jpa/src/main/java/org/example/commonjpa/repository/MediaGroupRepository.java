package org.example.commonjpa.repository;

import org.example.commonjpa.entity.MediaGroupModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaGroupRepository extends JpaRepository<MediaGroupModel, Long> {
    Optional<MediaGroupModel> findTop1ByChatIdAndItemIdOrderByCreatedDesc(Long chatId, Long mediaGroupId);
}
