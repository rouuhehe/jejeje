package com.dbp.legalcheck.application;

import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.chatSession.SessionResponseDTO;
import com.dbp.legalcheck.dto.message.MessageResponseDTO;
import com.dbp.legalcheck.domain.ai.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AiControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AiService aiService() {
            return org.mockito.Mockito.mock(AiService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AiService aiService;

    private User testUser;
    private UUID testSessionId;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFirstName("test");
        testUser.setLastName("test");
        testUser.setEmail("test@test.com");
        testUser.setPassword("test");
        testUser.setRole(UserRole.ADMINISTRADOR);

        testSessionId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );
    }

    @Test
    void createSession_shouldReturnCreatedSession() throws Exception {
        ChatSession mockSession = new ChatSession();
        mockSession.setId(testSessionId);
        mockSession.setUser(testUser);

        when(aiService.createSession(any(User.class))).thenReturn(mockSession);

        mockMvc.perform(post("/api/ai/chat/session/create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testSessionId.toString()));
    }

    @Test
    void generateResponse_shouldReturnString() throws Exception {
        String prompt = "Test prompt";
        String response = "Test response";

        when(aiService.generateResponse(
                any(User.class),
                eq(testSessionId),
                eq(prompt)))
                .thenReturn(response);

        mockMvc.perform(post("/api/ai/chat/message/{sessionId}", testSessionId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(prompt))
                .andExpect(status().isOk())
                .andExpect(content().string(response + "\n"));
    }

    @Test
    void listSessions_shouldReturnList() throws Exception {
        SessionResponseDTO sessionDTO = new SessionResponseDTO();
        sessionDTO.setId(testSessionId);
        sessionDTO.setStatus(ChatSessionStatus.OPEN);

        List<SessionResponseDTO> sessions = List.of(sessionDTO);

        when(aiService.listSessions(any(User.class))).thenReturn(sessions);

        mockMvc.perform(get("/api/ai/sessions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testSessionId.toString()))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void listSessionMessages_shouldReturnList() throws Exception {
        MessageResponseDTO messageDTO = new MessageResponseDTO();
        messageDTO.setId(UUID.randomUUID());
        messageDTO.setContent("Test message");
        messageDTO.setRole(MessageRole.USUARIO);

        List<MessageResponseDTO> messages = List.of(messageDTO);

        when(aiService.listSessionMessages(
                eq(testSessionId),
                any(User.class)))
                .thenReturn(messages);

        mockMvc.perform(get("/api/ai/sessions/{id}", testSessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test message"))
                .andExpect(jsonPath("$[0].role").value("USUARIO"));
    }

    @Test
    void closeSession_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/ai/sessions/{id}", testSessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}