package com.dbp.legalcheck.domain;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithDefaultValues() {
        User user = new User();
        assertThat(user.getRole()).isEqualTo(UserRole.USUARIO);
    }

    @Test
    void shouldHandleVerificationStatusCorrectly() {
        User user = new User();

        user.setVerificationId(UUID.randomUUID());
        assertThat(user.verified()).isFalse();

        user.setVerificationId(null);
        assertThat(user.verified()).isTrue();
    }

    @Test
    void shouldImplementUserDetailsCorrectly() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(UserRole.ABOGADO);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String username = user.getUsername();

        assertThat(authorities)
                .hasSize(1)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ABOGADO");
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void shouldManageTimestamps() {
        User user = new User();
        Instant now = Instant.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now.plusSeconds(10));

        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isAfter(user.getCreatedAt());
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());

        User user2 = new User();
        user2.setId(user1.getId());

        User user3 = new User();
        user3.setId(UUID.randomUUID());

        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void shouldHandleAccountStatusMethods() {
        User user = new User();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}