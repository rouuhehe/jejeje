package com.dbp.legalcheck.domain.ai;

import java.util.List;
import java.util.UUID;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.config.ai.AiConfig;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.chatSession.ChatSessionService;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.exception.chatSession.SessionNoOwnerException;
import com.dbp.legalcheck.exception.chatSession.SessionNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {
    private final AiConfig config;
    private final ChatCompletionsClient client;
    private final ChatSessionService sessionService;

    // POST /api/ai/chat/session/create
    public ChatSession createSession(User user) {
        return sessionService.createSession(user);
    }

    // POST /api/ai/chat/{id}
    public String generateResponse(User user, UUID sessionId, String prompt) {
        ChatSession session = sessionService.getSession(user, sessionId);

        if (!session.getUser().getId().equals(user.getId())) {
            throw new SessionNoOwnerException();
        }

        if (session.getStatus() == ChatSessionStatus.CLOSED) {
            throw new ClosedSessionException();
        }

        sessionService.saveMessage(session, MessageRole.USUARIO, prompt);

        List<ChatRequestMessage> messages = sessionService.getSessionMessages(user, sessionId, prompt);

        ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(config.getToken()))
                .endpoint(config.getEndpoint())
                .buildClient();

        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
        options.setModel(config.getModel());
        options.setMaxTokens(1024);

        ChatCompletions completions = client.complete(options);

        if (completions.getChoices().isEmpty()) {
            throw new AiNoResponseException();
        }

        String response = completions.getChoices().get(0).getMessage().getContent();

        sessionService.saveMessage(session, MessageRole.ASISTENTE, response);
        return response;
    }

    // GET /api/ai/sessions/{id}
    public List<SessionResponseDTO> listSessions(User user) {
        return sessionService.getSessionsByUser(user);
    }

    // GET /api/ai/sessions/{id}
    public List<MessageResponseDTO> listSessionMessages(UUID sessionId, User user) {
        return sessionService.listMessagesBySession(sessionId, user);
    }

    // PUT /api/ai/sessions/{id}
    public void closeSession(UUID id, User user) {
        ChatSession session = sessionService.getSessionById(id)
                .orElseThrow(SessionNotFoundException::new);

        if (!session.getUser().getId().equals(user.getId())) {
            throw new SessionNoOwnerException();
        }

        session.setStatus(ChatSessionStatus.CLOSED);
        sessionService.updateSession(session);
    }
}
