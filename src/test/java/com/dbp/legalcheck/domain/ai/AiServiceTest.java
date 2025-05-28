package com.dbp.legalcheck.domain.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.config.ai.AiConfig;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.chatSession.ChatSessionService;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.exception.chatSession.SessionNoOwnerException;
import com.dbp.legalcheck.exception.chatSession.SessionNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private AiConfig aiConfig;

    @Mock
    private ChatSessionService sessionService;

    @Mock
    private ChatCompletionsClient client;

    @InjectMocks
    private AiService aiService;

    private UUID sessionId;
    private User user;
    private ChatSession session;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        user = new User();
        user.setId(UUID.randomUUID());

        session = ChatSession.builder()
                .id(sessionId)
                .user(user)
                .status(ChatSessionStatus.OPEN)
                .build();

        lenient().when(sessionService.getOrCreateSession(user, sessionId)).thenReturn(session);
    }

    @Test
    void testGenerateResponse_ShouldReturnAIResponse() {
        // Arrange
        String prompt = "¿Cuál es el plazo para apelar una sentencia civil?";
        String aiReply = "El plazo es de 10 días hábiles según el artículo X del Código Procesal Civil.";

        ChatRequestMessage userMessage = new ChatRequestUserMessage(prompt);
        List<ChatRequestMessage> requestMessages = List.of(userMessage);

        when(sessionService.getSessionMessages(user, sessionId, prompt)).thenReturn(requestMessages);

        ChatResponseMessage responseMessage = mock(ChatResponseMessage.class);
        when(responseMessage.getContent()).thenReturn(aiReply);

        ChatChoice choice = mock(ChatChoice.class);
        when(choice.getMessage()).thenReturn(responseMessage);

        ChatCompletions completions = mock(ChatCompletions.class);
        when(completions.getChoices()).thenReturn(List.of(choice));

        when(client.complete(any(ChatCompletionsOptions.class))).thenReturn(completions);

        when(aiConfig.getModel()).thenReturn("deepseek/DeepSeek-V3-0324");

        // Act
        String response = aiService.generateResponse(user, sessionId, prompt);

        // Assert
        assertEquals(aiReply, response);
        verify(sessionService).saveMessage(session, MessageRole.USUARIO, prompt);
        verify(sessionService).saveMessage(session, MessageRole.ASISTENTE, aiReply);
    }

    @Test
    void testGenerateResponse_ShouldThrow_WhenUserIsNotOwner() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        // Mock session with a different owner
        ChatSession mockSession = mock(ChatSession.class);
        User sessionOwner = mock(User.class);
        when(sessionOwner.getId()).thenReturn(UUID.randomUUID()); // diferente a otherUser.getId()
        when(mockSession.getUser()).thenReturn(sessionOwner);

        when(sessionService.getOrCreateSession(otherUser, sessionId)).thenReturn(mockSession);

        // Act & Assert
        assertThrows(SessionNoOwnerException.class, () -> {
            aiService.generateResponse(otherUser, sessionId, "¿Hola?");
        });
    }

    @Test
    void testListSessions_ShouldReturnUserSessions() {
        List<ChatSession> expectedSessions = List.of(session);
        when(sessionService.getSessionsByUser(user)).thenReturn(expectedSessions);

        List<ChatSession> result = aiService.listSessions(user);

        assertEquals(expectedSessions, result);
    }

    @Test
    void testListSessionMessages_ShouldReturnMessages() {
        List<Message> expectedMessages = List.of(
                Message.builder().content("Hola").role(MessageRole.USUARIO).build()
        );

        when(sessionService.listMessagesBySession(sessionId, user)).thenReturn(expectedMessages);

        List<Message> result = aiService.listSessionMessages(sessionId, user);

        assertEquals(expectedMessages, result);
    }

    @Test
    void testCloseSession_ShouldUpdateStatus() {
        when(sessionService.getSessionById(sessionId)).thenReturn(java.util.Optional.of(session));

        aiService.closeSession(sessionId, user);

        assertEquals(ChatSessionStatus.CLOSED, session.getStatus());
        verify(sessionService).updateSession(session);
    }

    @Test
    void testCloseSession_ShouldThrowIfNotOwner() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        when(sessionService.getSessionById(sessionId)).thenReturn(java.util.Optional.of(session));

        assertThrows(SessionNoOwnerException.class, () -> {
            aiService.closeSession(sessionId, otherUser);
        });
    }

    @Test
    void testCloseSession_ShouldThrowIfNotFound() {
        when(sessionService.getSessionById(sessionId)).thenReturn(java.util.Optional.empty());

        assertThrows(SessionNotFoundException.class, () -> {
            aiService.closeSession(sessionId, user);
        });
    }
}
