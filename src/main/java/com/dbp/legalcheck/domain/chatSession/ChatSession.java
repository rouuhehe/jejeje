package com.dbp.legalcheck.domain.chatSession;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.ChatSessionStatus;
import com.dbp.legalcheck.domain.message.Message;
import com.dbp.legalcheck.domain.user.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private ChatSessionStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Message> messages;
}
