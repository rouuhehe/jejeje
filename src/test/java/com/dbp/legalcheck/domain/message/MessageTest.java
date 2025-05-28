package com.dbp.legalcheck.domain.message;

import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    @Test
    void shouldCreateMessageWithBuilder() {
        UUID id = UUID.randomUUID();
        ChatSession session = new ChatSession();
        MessageRole role = MessageRole.USUARIO;
        Instant now = Instant.now();
        String content = "Hola, ¿cómo puedo ayudarte?";

        Message message = Message.builder()
                .id(id)
                .session(session)
                .role(role)
                .createdAt(now)
                .content(content)
                .build();

        assertThat(message.getId()).isEqualTo(id);
        assertThat(message.getSession()).isEqualTo(session);
        assertThat(message.getRole()).isEqualTo(MessageRole.USUARIO);
        assertThat(message.getCreatedAt()).isEqualTo(now);
        assertThat(message.getContent()).isEqualTo(content);
    }
}
