package com.dbp.legalcheck.application.ai;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.domain.ai.AiService;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.chatSession.SessionResponseDTO;
import com.dbp.legalcheck.dto.message.MessageResponseDTO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chat/session/create")
    public ChatSession chat(@AuthenticationPrincipal User user) {
        return aiService.createSession(user);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chat/message/{sessionId}")
    public String chat(@AuthenticationPrincipal User user,
            @PathVariable UUID sessionId, @RequestBody String prompt) {
        return aiService.generateResponse(user, sessionId, prompt) + "\n";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sessions")
    public List<SessionResponseDTO> listSessions(@AuthenticationPrincipal User user) {
        return aiService.listSessions(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sessions/{id}")
    public List<MessageResponseDTO> listSessionMessages(@PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return aiService.listSessionMessages(id, currentUser);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/sessions/{id}")
    public void closeSession(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        aiService.closeSession(id, currentUser);
    }
}
