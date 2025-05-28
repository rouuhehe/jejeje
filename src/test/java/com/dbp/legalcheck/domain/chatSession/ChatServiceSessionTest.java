package com.dbp.legalcheck.domain.chatSession;

import com.azure.ai.inference.models.ChatRequestMessage;
import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.exception.chatSession.SessionNoOwnerException;
import com.dbp.legalcheck.exception.chatSession.SessionNotFoundException;
import com.dbp.legalcheck.infrastructure.chatSession.ChatSessionRepository;
import com.dbp.legalcheck.infrastructure.message.MessageRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatSessionService chatSessionService;

    private User user;
    private ChatSession session;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());

        session = ChatSession.builder()
                .id(UUID.randomUUID())
                .user(user)
                .status(ChatSessionStatus.OPEN)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void getOrCreateSessionShouldReturnExistingSession() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        ChatSession result = chatSessionService.getOrCreateSession(user, session.getId());

        assertEquals(session, result);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getOrCreateSessionShouldCreateNewSessionIfNotExists() {
        when(sessionRepository.findById(any())).thenReturn(Optional.empty());
        when(sessionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ChatSession result = chatSessionService.getOrCreateSession(user, UUID.randomUUID());

        assertEquals(user, result.getUser());
        assertEquals(ChatSessionStatus.OPEN, result.getStatus());
        verify(sessionRepository).save(any());
    }

    @Test
    void listMessagesBySessionShouldReturnMessagesIfUserOwnsSession() {
        List<Message> expectedMessages = List.of(
                new Message(session, MessageRole.USUARIO, "Hola", Instant.now()));

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(expectedMessages);

        List<Message> result = chatSessionService.listMessagesBySession(session.getId(), user);

        assertEquals(expectedMessages, result);
    }

    @Test
    void listMessagesBySessionShouldThrowIfSessionNotFound() {
        when(sessionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class,
                () -> chatSessionService.listMessagesBySession(UUID.randomUUID(), user));
    }

    @Test
    void listMessagesBySessionShouldThrowIfUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        assertThrows(SessionNoOwnerException.class,
                () -> chatSessionService.listMessagesBySession(session.getId(), anotherUser));
    }

    @Test
    void saveMessageShouldStoreMessage() {
        chatSessionService.saveMessage(session, MessageRole.USUARIO, "Hola");

        verify(messageRepository).save(argThat(m ->
                m.getContent().equals("Hola") &&
                        m.getRole() == MessageRole.USUARIO &&
                        m.getSession().equals(session)
        ));
    }

    @Test
    void getSessionMessagesShouldBuildCorrectMessageList() {
        Message m1 = new Message(session, MessageRole.USUARIO, "¿Cuáles son mis derechos laborales?", Instant.now());
        Message m2 = new Message(session, MessageRole.ASISTENTE, "Tienes derecho a... ", Instant.now());

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(List.of(m1, m2));

        List<ChatRequestMessage> result = chatSessionService.getSessionMessages(user, session.getId(), "nuevo mensaje");

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(m -> m instanceof com.azure.ai.inference.models.ChatRequestSystemMessage));
        assertTrue(result.stream().anyMatch(m -> m instanceof com.azure.ai.inference.models.ChatRequestUserMessage));
        assertTrue(result.stream().anyMatch(m -> m instanceof com.azure.ai.inference.models.ChatRequestAssistantMessage));
    }

    @Test
    void updateSessionShouldSaveSession() {
        chatSessionService.updateSession(session);
        verify(sessionRepository).save(session);
    }

    @Test
    void getSessionsByUserShouldCallRepository() {
        chatSessionService.getSessionsByUser(user);
        verify(sessionRepository).findByUser(user);
    }
}
