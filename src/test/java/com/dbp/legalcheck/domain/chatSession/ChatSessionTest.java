package com.dbp.legalcheck.domain.chatSession;

import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatSessionTest {

    @Test
    void buildershouldCreateChatSessionCorrectly() {
        UUID sessionId = UUID.randomUUID();
        User user = new User();
        Instant now = Instant.now();
        Message message1 = new Message();
        Message message2 = new Message();

        ChatSession session = ChatSession.builder()
                .id(sessionId)
                .user(user)
                .createdAt(now)
                .status(ChatSessionStatus.OPEN)
                .messages(List.of(message1, message2))
                .build();

        assertEquals(sessionId, session.getId());
        assertEquals(user, session.getUser());
        assertEquals(now, session.getCreatedAt());
        assertEquals(ChatSessionStatus.OPEN, session.getStatus());
        assertEquals(2, session.getMessages().size());
    }

    @Test
    void noArgsConstructorShouldCreateEmptySession() {
        ChatSession session = new ChatSession();
        assertNull(session.getId());
        assertNull(session.getUser());
        assertNull(session.getCreatedAt());
        assertNull(session.getStatus());
        assertNull(session.getMessages());
    }

    @Test
    void settersAndGettersShouldWorkProperly() {
        ChatSession session = new ChatSession();

        UUID id = UUID.randomUUID();
        User user = new User();
        Instant now = Instant.now();
        Message message = new Message();

        session.setId(id);
        session.setUser(user);
        session.setCreatedAt(now);
        session.setStatus(ChatSessionStatus.CLOSED);
        session.setMessages(List.of(message));

        assertEquals(id, session.getId());
        assertEquals(user, session.getUser());
        assertEquals(now, session.getCreatedAt());
        assertEquals(ChatSessionStatus.CLOSED, session.getStatus());
        assertEquals(1, session.getMessages().size());
    }
}
