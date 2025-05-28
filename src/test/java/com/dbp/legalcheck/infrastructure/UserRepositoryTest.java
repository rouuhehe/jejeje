package com.dbp.legalcheck.infrastructure;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.infrastructure.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // Clear data before each test
        userRepository.deleteAll();

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setPhoneNumber("+1234567890");
        user.setRole(UserRole.ADMINISTRADOR);
        user.setVerificationId(UUID.randomUUID());

        savedUser = userRepository.save(user);
    }

    @Test
    void findByEmailShouldReturnUserWhenUserExists() {

        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void findByEmaiShouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByVerificationIdShouldReturnUserWhenVerificationIdExists() {
        Optional<User> foundUser = userRepository.findByVerificationId(savedUser.getVerificationId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getVerificationId()).isEqualTo(savedUser.getVerificationId());
    }

    @Test
    void findByVerificationIdShouldReturnEmptyWhenVerificationIdDoesNotExist() {
        Optional<User> foundUser = userRepository.findByVerificationId(UUID.randomUUID());
        assertThat(foundUser).isEmpty();
    }

    @Test
    void existsByEmailShouldReturnTrueWhenEmailExists() {
        boolean exists = userRepository.existsByEmail("john.doe@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmailShouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByRoleShouldReturnTrueWhenRoleExists() {
        boolean exists = userRepository.existsByRole(UserRole.ADMINISTRADOR);
        assertThat(exists).isTrue();
    }

    @Test
    void existsByRoleShouldReturnFalseWhenRoleDoesNotExist() {
        boolean exists = userRepository.existsByRole(UserRole.USUARIO);
        assertThat(exists).isFalse();
    }

    @Test
    void existsByRoleAndIdNotShouldReturnFalseWhenOnlyUserWithThatRoleExists() {
        boolean exists = userRepository.existsByRoleAndIdNot(UserRole.ADMINISTRADOR, savedUser.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void existsByRoleAndIdNotShouldReturnTrueWhenAnotherUserWithThatRoleExists() {
        User anotherAdmin = new User();
        anotherAdmin.setFirstName("Jane");
        anotherAdmin.setLastName("Doe");
        anotherAdmin.setEmail("jane.doe@example.com");
        anotherAdmin.setPassword("password123");
        anotherAdmin.setPhoneNumber("+987654321");
        anotherAdmin.setRole(UserRole.ADMINISTRADOR);
        userRepository.save(anotherAdmin);

        boolean exists = userRepository.existsByRoleAndIdNot(UserRole.ADMINISTRADOR, savedUser.getId());
        assertThat(exists).isTrue();
    }
}