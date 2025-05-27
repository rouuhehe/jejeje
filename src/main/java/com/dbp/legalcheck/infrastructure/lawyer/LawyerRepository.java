package com.dbp.legalcheck.infrastructure.lawyer;

import java.util.UUID;

import com.dbp.legalcheck.domain.lawyer.Lawyer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {
    boolean existsByEmail(String email);
}
