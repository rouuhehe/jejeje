package com.dbp.legalcheck.infrastructure;

import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.infrastructure.chatSession.ChatSessionRepository;
import com.dbp.legalcheck.infrastructure.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatSessionRepositoryTest {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private ChatSession activeSession;
    private ChatSession closedSession;

    @BeforeEach
    void setUp() {
        chatSessionRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test.user@example.com");
        testUser.setPassword("password123");
        testUser.setRole(UserRole.USUARIO);
        userRepository.save(testUser);

        activeSession = ChatSession.builder()
                .user(testUser)
                .status(ChatSessionStatus.OPEN)
                .build();
        chatSessionRepository.save(activeSession);

        closedSession = ChatSession.builder()
                .user(testUser)
                .status(ChatSessionStatus.CLOSED)
                .build();
        chatSessionRepository.save(closedSession);
    }

    @Test
    void findByUserShouldReturnAllSessionsForUser() {
        List<ChatSession> sessions = chatSessionRepository.findByUser(testUser);

        assertThat(sessions)
                .hasSize(2)
                .extracting(ChatSession::getId)
                .containsExactlyInAnyOrder(activeSession.getId(), closedSession.getId());
    }

    @Test
    void findByUserShouldReturnEmptyListForNonExistentUser() {
        User nonExistentUser = new User();
        nonExistentUser.setId(UUID.randomUUID());

        List<ChatSession> sessions = chatSessionRepository.findByUser(nonExistentUser);

        assertThat(sessions).isEmpty();
    }

    @Test
    void shouldPersistChatSessionWithCorrectProperties() {
        entityManager.flush();
        entityManager.clear();

        ChatSession retrieved = chatSessionRepository.findById(activeSession.getId()).orElseThrow();

        assertThat(retrieved.getUser()).isNotNull();
        assertThat(retrieved.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(retrieved.getStatus()).isEqualTo(ChatSessionStatus.OPEN);
        assertThat(retrieved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateChatSessionStatus() {
        ChatSession toUpdate = chatSessionRepository.findById(activeSession.getId()).orElseThrow();
        toUpdate.setStatus(ChatSessionStatus.CLOSED);

        chatSessionRepository.save(toUpdate);
        ChatSession updated = chatSessionRepository.findById(activeSession.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(ChatSessionStatus.CLOSED);
    }

    @Test
    void shouldDeleteChatSession() {

        chatSessionRepository.deleteById(activeSession.getId());

        assertThat(chatSessionRepository.findById(activeSession.getId())).isEmpty();
    }

    @Test
    void shouldAutomaticallySetCreationTimestamp() {
        Instant before = Instant.now();

        ChatSession newSession = ChatSession.builder()
                .user(testUser)
                .status(ChatSessionStatus.OPEN)
                .build();

        chatSessionRepository.save(newSession);

        entityManager.flush();
        entityManager.clear();
        ChatSession reloaded = chatSessionRepository.findById(newSession.getId()).orElseThrow();

        assertThat(reloaded.getCreatedAt()).isNotNull();
        assertThat(reloaded.getCreatedAt()).isBetween(before, Instant.now());
    }
}