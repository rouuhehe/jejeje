package com.dbp.legalcheck.domain.chatSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.azure.ai.inference.models.ChatRequestAssistantMessage;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.chatSession.SessionResponseDTO;
import com.dbp.legalcheck.dto.message.MessageResponseDTO;
import com.dbp.legalcheck.exception.chatSession.SessionNoOwnerException;
import com.dbp.legalcheck.exception.chatSession.SessionNotFoundException;
import com.dbp.legalcheck.infrastructure.chatSession.ChatSessionRepository;
import com.dbp.legalcheck.infrastructure.message.MessageRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatSessionService {
    private final ChatSessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    public List<Message> listSessionHistory(ChatSession session) {
        return messageRepository.findBySessionOrderByCreatedAtAsc(session);
    }

    public void saveMessage(ChatSession session, MessageRole role, String content) {
        messageRepository.save(Message
                .builder()
                .session(session)
                .role(role)
                .content(content)
                .build());
    }

    public List<MessageResponseDTO> listMessagesBySession(UUID sessionId, User user) {
        ChatSession session = getSessionById(sessionId)
                .orElseThrow(SessionNotFoundException::new);

        if (!session.getUser().getId().equals(user.getId())) {
            throw new SessionNoOwnerException();
        }

        return listSessionHistory(session)
                .stream().map(MessageResponseDTO::new).toList();
    }

    public ChatSession createSession(User user) {
        // TODO: trigger session created event
        return sessionRepository.save(ChatSession
                .builder()
                .user(user)
                .status(ChatSessionStatus.OPEN)
                .build());
    }

    public ChatSession getSession(User user, UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);
    }

    public List<ChatRequestMessage> getSessionMessages(User user, UUID sessionId, String newPrompt) {
        ChatSession session = getSession(user, sessionId);
        List<Message> history = listSessionHistory(session);
        List<ChatRequestMessage> messages = new ArrayList<>();

        messages.add(new ChatRequestSystemMessage(
                "Eres un asistente legal virtual llamado LegalCheck, eres de Perú, por lo que unicamente estás capacitado de responder preguntas sobre temas legales de Perú, además estás especializado en brindar orientación clara, precisa y accesible sobre temas legales. Aunque los usuarios pueden expresarse de forma informal o coloquial, tú debes responder siempre con lenguaje profesional, respetuoso y centrado en lo legal. No imites el tono ni expresiones informales del usuario. Tu objetivo es ayudar de manera concreta, sin desviarte del tema ni inventar información.\r\n"
                        + //
                        "\r\n" + //
                        "Si no tienes certeza o no puedes ofrecer una solución definitiva, indica que el usuario debe consultar con un abogado especializado, de forma responsable.\r\n"
                        + //
                        "\r\n" + //
                        "Usa viñetas, listas o ejemplos si eso ayuda a que el usuario entienda mejor su situación."));

        messages.addAll(history.stream().map(m -> {
            return m.getRole() == MessageRole.USUARIO
                    ? new ChatRequestUserMessage(m.getContent())
                    : new ChatRequestAssistantMessage(m.getContent());
        }).toList());

        messages.add(new ChatRequestUserMessage(newPrompt));
        return messages;
    }

    public List<SessionResponseDTO> getSessionsByUser(User user) {
        return sessionRepository.findByUser(user)
                .stream().map(SessionResponseDTO::new).toList();
    }

    public Optional<ChatSession> getSessionById(UUID id) {
        return sessionRepository.findById(id);
    }

    public void updateSession(ChatSession session) {
        sessionRepository.save(session);
    }
}
