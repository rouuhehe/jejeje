package com.dbp.legalcheck.infrastructure;

import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.common.enums.MessageRole;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.chatSession.ChatSession;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.infrastructure.chatSession.ChatSessionRepository;
import com.dbp.legalcheck.infrastructure.message.MessageRepository;
import com.dbp.legalcheck.infrastructure.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    private ChatSession testSession;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        chatSessionRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test.user@example.com");
        testUser.setPassword("password123");
        testUser.setRole(UserRole.USUARIO);
        userRepository.save(testUser);

        testSession = ChatSession.builder()
                .user(testUser)
                .status(ChatSessionStatus.OPEN)
                .build();
        chatSessionRepository.save(testSession);

        message1 = Message.builder()
                .session(testSession)
                .role(MessageRole.USUARIO)
                .content("First message")
                .build();
        messageRepository.save(message1);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        message2 = Message.builder()
                .session(testSession)
                .role(MessageRole.ASISTENTE)
                .content("Second message")
                .build();
        messageRepository.save(message2);
    }

    @Test
    void findBySessionOrderByCreatedAtAscShouldReturnMessagesInCorrectOrder() {
        List<Message> messages = messageRepository.findBySessionOrderByCreatedAtAsc(testSession);

        assertThat(messages)
                .hasSize(2)
                .extracting(Message::getId)
                .containsExactly(message1.getId(), message2.getId());
    }

    @Test
    void findBySessionOrderByCreatedAtAscShouldReturnEmptyListForNonExistentSession() {

        ChatSession nonExistentSession = ChatSession.builder()
                .id(UUID.randomUUID())
                .build();

        List<Message> messages = messageRepository.findBySessionOrderByCreatedAtAsc(nonExistentSession);

        assertThat(messages).isEmpty();
    }

    @Test
    void shouldPersistMessageWithAllProperties() {
        entityManager.flush();
        entityManager.clear();
        Message retrieved = messageRepository.findById(message1.getId()).orElseThrow();

        assertThat(retrieved.getContent()).isEqualTo("First message");
        assertThat(retrieved.getRole()).isEqualTo(MessageRole.USUARIO);
        assertThat(retrieved.getSession().getId()).isEqualTo(testSession.getId());
        assertThat(retrieved.getCreatedAt()).isNotNull();
        assertThat(retrieved.getCreatedAt())
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void shouldUpdateMessageContent() {

        Message toUpdate = messageRepository.findById(message1.getId()).orElseThrow();
        toUpdate.setContent("Updated content");

        messageRepository.save(toUpdate);
        Message updated = messageRepository.findById(message1.getId()).orElseThrow();

        assertThat(updated.getContent()).isEqualTo("Updated content");
    }

    @Test
    void shouldDeleteMessage() {

        messageRepository.deleteById(message1.getId());

        assertThat(messageRepository.findById(message1.getId())).isEmpty();
    }

    @Test
    void shouldAutomaticallySetCreationTimestamp() {
        Instant beforeCreation = Instant.now();
        Message newMessage = Message.builder()
                .session(testSession)
                .role(MessageRole.USUARIO)
                .content("New message")
                .build();

        messageRepository.save(newMessage);
        entityManager.flush();
        entityManager.clear();
        Message reloaded = messageRepository.findById(newMessage.getId()).orElseThrow();

        assertThat(reloaded.getCreatedAt())
                .isBetween(beforeCreation, Instant.now());
    }

    @Test
    void shouldMaintainSessionRelationship() {

        Message retrieved = messageRepository.findById(message1.getId()).orElseThrow();


        assertThat(retrieved.getSession().getId()).isEqualTo(testSession.getId());
        assertThat(retrieved.getSession().getStatus()).isEqualTo(ChatSessionStatus.OPEN);
    }
}