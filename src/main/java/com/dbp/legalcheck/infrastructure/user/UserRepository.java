package com.dbp.legalcheck.infrastructure.user;

import java.util.Optional;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationId(UUID verificationId);

    boolean existsByEmail(String email);

    boolean existsByRole(UserRole role);

    boolean existsByRoleAndIdNot(UserRole role, UUID id);
}
