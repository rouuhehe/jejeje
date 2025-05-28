package com.dbp.legalcheck.domain.chatSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.exception.chatSession.SessionNoOwnerException;
import com.dbp.legalcheck.exception.chatSession.SessionNotFoundException;
import com.dbp.legalcheck.infrastructure.chatSession.ChatSessionRepository;
import com.dbp.legalcheck.infrastructure.message.MessageRepository;
import com.dbp.legalcheck.dto.message.MessageResponseDTO;
import com.dbp.legalcheck.dto.chatSession.SessionResponseDTO;
import com.dbp.legalcheck.common.enums.ChatSessionStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatSessionService chatSessionService;

    private User user;
    private ChatSession session;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        sessionId = UUID.randomUUID();
        session = ChatSession.builder()
                .id(sessionId)
                .user(user)
                .status(ChatSessionStatus.OPEN)
                .build();
    }

    @Test
    void listSessionHistory_ReturnsMessages() {
        List<Message> messages = List.of(
                Message.builder().content("Hola").role(MessageRole.USUARIO).session(session).build(),
                Message.builder().content("Respuesta").role(MessageRole.ASISTENTE).session(session).build()
        );

        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(messages);

        List<Message> result = chatSessionService.listSessionHistory(session);

        assertEquals(2, result.size());
        verify(messageRepository).findBySessionOrderByCreatedAtAsc(session);
    }

    @Test
    void saveMessage_SavesCorrectly() {
        String content = "Test message";

        chatSessionService.saveMessage(session, MessageRole.USUARIO, content);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());

        Message savedMessage = captor.getValue();
        assertEquals(content, savedMessage.getContent());
        assertEquals(MessageRole.USUARIO, savedMessage.getRole());
        assertEquals(session, savedMessage.getSession());
    }

    @Test
    void listMessagesBySession_WhenSessionExistsAndUserIsOwner_ReturnsDTOs() {
        List<Message> messages = List.of(
                Message.builder().content("Hola").role(MessageRole.USUARIO).session(session).build()
        );

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(messages);

        List<MessageResponseDTO> dtos = chatSessionService.listMessagesBySession(sessionId, user);

        assertEquals(1, dtos.size());
        assertEquals("Hola", dtos.get(0).getContent());
    }

    @Test
    void listMessagesBySession_WhenSessionNotFound_Throws() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class,
                () -> chatSessionService.listMessagesBySession(sessionId, user));
    }

    @Test
    void listMessagesBySession_WhenUserIsNotOwner_Throws() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(SessionNoOwnerException.class,
                () -> chatSessionService.listMessagesBySession(sessionId, otherUser));
    }

    @Test
    void createSession_SavesAndReturns() {
        when(sessionRepository.save(any(ChatSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatSession created = chatSessionService.createSession(user);

        assertEquals(user, created.getUser());
        assertEquals(ChatSessionStatus.OPEN, created.getStatus());
        verify(sessionRepository).save(any(ChatSession.class));
    }

    @Test
    void getSessionById_WhenExists_ReturnsOptional() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        Optional<ChatSession> result = chatSessionService.getSessionById(sessionId);

        assertTrue(result.isPresent());
        assertEquals(session, result.get());
    }

    @Test
    void getSessionById_WhenNotExists_ReturnsEmpty() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        Optional<ChatSession> result = chatSessionService.getSessionById(sessionId);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateSession_SavesSession() {
        chatSessionService.updateSession(session);

        verify(sessionRepository).save(session);
    }
}
