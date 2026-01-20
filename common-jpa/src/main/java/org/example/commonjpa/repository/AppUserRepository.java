package org.example.commonjpa.repository;

import org.example.commonjpa.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUserId(Long userId);
    Boolean existsBySessionId(Long sessionId);
}
