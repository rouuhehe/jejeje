package com.dbp.legalcheck.domain.user;

import com.dbp.legalcheck.common.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldCreateUserAndCheckFields() {
        UUID id = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        User user = new User();
        user.setId(id);
        user.setFirstName("Ana");
        user.setLastName("Pérez");
        user.setEmail("ana@example.com");
        user.setVerificationId(verificationId);
        user.setPassword("securepassword123");
        user.setPhoneNumber("123456789");
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        user.setRole(UserRole.USUARIO);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getFirstName()).isEqualTo("Ana");
        assertThat(user.getLastName()).isEqualTo("Pérez");
        assertThat(user.getEmail()).isEqualTo("ana@example.com");
        assertThat(user.getVerificationId()).isEqualTo(verificationId);
        assertThat(user.getPassword()).isEqualTo("securepassword123");
        assertThat(user.getPhoneNumber()).isEqualTo("123456789");
        assertThat(user.getRole()).isEqualTo(UserRole.USUARIO);
        assertThat(user.getUsername()).isEqualTo("ana@example.com");
        assertThat(user.getAuthorities())
                .extracting(a -> a.getAuthority())
                .contains("USUARIO");
        assertThat(user.verified()).isFalse();
    }

    @Test
    void verifiedShouldReturnTrueIfNoVerificationId() {
        User user = new User();
        user.setVerificationId(null);

        assertThat(user.verified()).isTrue();
    }
}
