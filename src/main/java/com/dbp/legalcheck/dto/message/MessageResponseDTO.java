package com.dbp.legalcheck.dto.message;

import java.time.Instant;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.domain.message.Message;

import lombok.Data;

@Data
public class MessageResponseDTO {
    private UUID id;
    private MessageRole role;
    private Instant createdAt;
    private String content;

    public MessageResponseDTO(Message message) {
        this.id = message.getId();
        this.role = message.getRole();
        this.createdAt = message.getCreatedAt();
        this.content = message.getContent();
    }
}
