package com.dbp.legalcheck.infrastructure.chatSession;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByUser(User user);
}
