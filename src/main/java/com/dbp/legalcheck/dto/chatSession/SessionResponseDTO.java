package com.dbp.legalcheck.dto.chatSession;

import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.domain.chatSession.ChatSession;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class SessionResponseDTO {
    private UUID id;
    private Instant createdAt;
    private ChatSessionStatus status;

    public SessionResponseDTO(ChatSession session) {
        this.id = session.getId();
        this.createdAt = session.getCreatedAt();
        this.status = session.getStatus();
    }
}
