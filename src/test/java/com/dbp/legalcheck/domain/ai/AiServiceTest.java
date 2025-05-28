package com.dbp.legalcheck.domain.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.config.ai.AiConfig;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.chatSession.ChatSessionService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.chatSession.SessionResponseDTO;
import com.dbp.legalcheck.dto.message.MessageResponseDTO;
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
    private ChatSessionService sessionService;

    @Mock
    private ChatCompletionsClient client;

    @Mock
    private AiConfig aiConfig;

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
    }

    @Test
    void testGenerateResponseShouldReturnAIResponse() {
        String prompt = "¿Cuál es el plazo para apelar una sentencia civil?";
        String aiReply = "El plazo es de 10 días hábiles según el artículo X del Código Procesal Civil.";

        when(sessionService.getSession(user, sessionId)).thenReturn(session);

        List<ChatRequestMessage> requestMessages = List.of(new ChatRequestUserMessage(prompt));
        when(sessionService.getSessionMessages(user, sessionId, prompt)).thenReturn(requestMessages);

        ChatResponseMessage responseMessage = mock(ChatResponseMessage.class);
        when(responseMessage.getContent()).thenReturn(aiReply);

        ChatChoice choice = mock(ChatChoice.class);
        when(choice.getMessage()).thenReturn(responseMessage);

        ChatCompletions completions = mock(ChatCompletions.class);
        when(completions.getChoices()).thenReturn(List.of(choice));

        when(client.complete(any(ChatCompletionsOptions.class))).thenReturn(completions);
        when(aiConfig.getModel()).thenReturn("deepseek/DeepSeek-V3-0324");

        String response = aiService.generateResponse(user, sessionId, prompt);

        assertEquals(aiReply, response);

        verify(sessionService).saveMessage(session, MessageRole.USUARIO, prompt);
        verify(sessionService).saveMessage(session, MessageRole.ASISTENTE, aiReply);
    }

    @Test
    void testGenerateResponseShouldThrow_WhenUserIsNotOwner() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        ChatSession mockSession = mock(ChatSession.class);
        User sessionOwner = mock(User.class);
        when(sessionOwner.getId()).thenReturn(UUID.randomUUID());
        when(mockSession.getUser()).thenReturn(sessionOwner);

        when(sessionService.getSession(otherUser, sessionId)).thenReturn(mockSession);

        assertThrows(SessionNoOwnerException.class, () -> {
            aiService.generateResponse(otherUser, sessionId, "¿Hola?");
        });
    }

    @Test
    void testListSessionsShouldReturnUserSessions() {
        SessionResponseDTO expectedSession = new SessionResponseDTO();
        expectedSession.setId(sessionId);
        expectedSession.setStatus(ChatSessionStatus.OPEN);

        List<SessionResponseDTO> expectedSessions = List.of(expectedSession);
        when(sessionService.getSessionsByUser(user)).thenReturn(expectedSessions);

        List<SessionResponseDTO> result = aiService.listSessions(user);

        assertEquals(expectedSessions, result);
    }

    @Test
    void testListSessionMessagesShouldReturnMessages() {
        MessageResponseDTO expectedMessage = new MessageResponseDTO();
        expectedMessage.setContent("Hola");
        expectedMessage.setRole(MessageRole.USUARIO);

        List<MessageResponseDTO> expectedMessages = List.of(expectedMessage);
        when(sessionService.listMessagesBySession(sessionId, user)).thenReturn(expectedMessages);

        List<MessageResponseDTO> result = aiService.listSessionMessages(sessionId, user);

        assertEquals(expectedMessages, result);
    }

    @Test
    void testCloseSessionShouldUpdateStatus() {
        when(sessionService.getSessionById(sessionId)).thenReturn(Optional.of(session));

        aiService.closeSession(sessionId, user);

        assertEquals(ChatSessionStatus.CLOSED, session.getStatus());
        verify(sessionService).updateSession(session);
    }

    @Test
    void testCloseSessionShouldThrowIfNotOwner() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        when(sessionService.getSessionById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(SessionNoOwnerException.class, () -> {
            aiService.closeSession(sessionId, otherUser);
        });
    }

    @Test
    void testCloseSessionShouldThrowIfNotFound() {
        when(sessionService.getSessionById(sessionId)).thenReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class, () -> {
            aiService.closeSession(sessionId, user);
        });
    }
}
