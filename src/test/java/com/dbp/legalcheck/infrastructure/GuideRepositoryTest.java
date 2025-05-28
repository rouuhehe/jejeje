package com.dbp.legalcheck.infrastructure;

import com.dbp.legalcheck.common.enums.GuideType;
import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.guide.Guide;
import com.dbp.legalcheck.domain.lawyer.Lawyer;
import com.dbp.legalcheck.infrastructure.guide.GuideRepository;
import com.dbp.legalcheck.infrastructure.lawyer.LawyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GuideRepositoryTest {
    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private LawyerRepository lawyerRepository;

    private Guide savedGuide;

    @BeforeEach
    void setUp() {
        guideRepository.deleteAll();
        lawyerRepository.deleteAll();

        Lawyer author = new Lawyer();
        author.setFirstName("Laura");
        author.setLastName("Fernández");
        author.setEmail("laura.fernandez@legal.com");
        author.setPassword("securePass123");
        author.setPhoneNumber("+5491177777777");
        author.setRole(UserRole.ABOGADO);
        author.setTuitionNumber("MP-666666");
        author.setYearExperience(5);
        author.setSpecializations(new ArrayList<>(List.of(LawyerSpecialization.DERECHO_PENAL)));
        lawyerRepository.save(author);

        Guide guide = new Guide();
        guide.setTitle("Guía Básica de Derecho Penal");
        guide.setContent("Introducción al derecho penal...");
        guide.setType(GuideType.PENAL);
        guide.setAuthor(author);

        savedGuide = guideRepository.saveAndFlush(guide);
    }

    @Test
    void shouldSaveAndRetrieveGuide() {
        Guide foundGuide = guideRepository.findById(savedGuide.getId()).orElseThrow();

        assertThat(foundGuide.getTitle()).isEqualTo("Guía Básica de Derecho Penal");
        assertThat(foundGuide.getContent()).isEqualTo("Introducción al derecho penal...");
        assertThat(foundGuide.getType()).isEqualTo(GuideType.PENAL);
        assertThat(foundGuide.getCreatedAt()).isNotNull();
        assertThat(foundGuide.getUpdatedAt()).isNotNull();
    }

    @Test
    void existsByTitle_shouldReturnTrueForExistingTitle() {
        boolean exists = guideRepository.existsByTitle("Guía Básica de Derecho Penal");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByTitle_shouldReturnFalseForNonExistingTitle() {
        boolean exists = guideRepository.existsByTitle("Título que no existe");
        assertThat(exists).isFalse();
    }

    @Test
    void shouldEnforceTitleUniqueness() {
        Guide newGuide = new Guide();
        newGuide.setTitle("Guía Básica de Derecho Penal"); // Same title as savedGuide
        newGuide.setContent("Nuevo contenido");
        newGuide.setAuthor(savedGuide.getAuthor());

        assertThrows(DataIntegrityViolationException.class,
                () -> guideRepository.saveAndFlush(newGuide));
    }

    @Test
    void shouldDeleteGuide() {
        UUID guideId = savedGuide.getId();
        assertThat(guideRepository.findById(guideId)).isPresent();

        guideRepository.deleteById(guideId);

        assertThat(guideRepository.findById(guideId)).isEmpty();
    }

    @Test
    void shouldUpdateGuide() {
        Guide toUpdate = guideRepository.findById(savedGuide.getId()).orElseThrow();
        toUpdate.setTitle("Guía Actualizada de Derecho Penal");
        guideRepository.saveAndFlush(toUpdate);

        Guide updated = guideRepository.findById(savedGuide.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Guía Actualizada de Derecho Penal");
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(savedGuide.getUpdatedAt());
    }
}