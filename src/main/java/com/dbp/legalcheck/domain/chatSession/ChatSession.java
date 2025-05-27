package com.dbp.legalcheck.domain.chatSession;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private ChatSessionStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Message> messages;
}
