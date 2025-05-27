package com.dbp.legalcheck.infrastructure.message;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.message.Message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySessionOrderByCreatedAtAsc(ChatSession session);
}
