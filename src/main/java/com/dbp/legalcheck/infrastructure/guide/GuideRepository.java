package com.dbp.legalcheck.infrastructure.guide;

import java.util.UUID;

import com.dbp.legalcheck.domain.guide.Guide;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<Guide, UUID> {
    boolean existsByTitle(String title);
}
