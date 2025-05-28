package com.dbp.legalcheck.infrastructure;

import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.lawyer.Lawyer;
import com.dbp.legalcheck.infrastructure.lawyer.LawyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LawyerRepositoryTest {
    @Autowired
    private LawyerRepository lawyerRepository;

    private Lawyer savedLawyer;

    @BeforeEach
    void setUp() {
        lawyerRepository.deleteAll();

        Lawyer lawyer = new Lawyer();
        lawyer.setFirstName("Carlos");
        lawyer.setLastName("Pérez");
        lawyer.setEmail("carlos.perez@bufete.com");
        lawyer.setPassword("securePass123");
        lawyer.setPhoneNumber("+5491144444444");
        lawyer.setRole(UserRole.ABOGADO);
        lawyer.setTuitionNumber("MP-987654");
        lawyer.setYearExperience(8);
        lawyer.setSpecializations(new ArrayList<>(List.of(
                LawyerSpecialization.DERECHO_PENAL,
                LawyerSpecialization.DERECHO_MERCANTIL
        )));

        savedLawyer = lawyerRepository.save(lawyer);
    }

    @Test
    void shouldSaveAndRetrieveLawyerWithAllProperties() {
        Optional<Lawyer> foundLawyer = lawyerRepository.findById(savedLawyer.getId());

        assertThat(foundLawyer).isPresent();
        Lawyer lawyer = foundLawyer.get();

        assertThat(lawyer.getEmail()).isEqualTo("carlos.perez@bufete.com");
        assertThat(lawyer.getRole()).isEqualTo(UserRole.ABOGADO);
        assertThat(lawyer.getTuitionNumber()).isEqualTo("MP-987654");
        assertThat(lawyer.getYearExperience()).isEqualTo(8);
    }

    @Test
    void shouldPersistSpecializationsCollection() {
        Lawyer retrieved = lawyerRepository.findById(savedLawyer.getId()).orElseThrow();
        retrieved.setSpecializations(new ArrayList<>(List.of(
                LawyerSpecialization.DERECHO_PENAL,
                LawyerSpecialization.DERECHO_MERCANTIL
        )));

        assertThat(retrieved.getSpecializations())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        LawyerSpecialization.DERECHO_PENAL,
                        LawyerSpecialization.DERECHO_MERCANTIL
                );
    }

    @Test
    void shouldUpdateLawyerProperties() {
        Lawyer toUpdate = lawyerRepository.findById(savedLawyer.getId()).orElseThrow();
        toUpdate.setYearExperience(10);
        toUpdate.setSpecializations(new ArrayList<>(List.of(LawyerSpecialization.DERECHO_LABORAL)));
        lawyerRepository.save(toUpdate);

        Lawyer updated = lawyerRepository.findById(savedLawyer.getId()).orElseThrow();

        assertThat(updated.getYearExperience()).isEqualTo(10);
        assertThat(updated.getSpecializations())
                .containsExactly(LawyerSpecialization.DERECHO_LABORAL);
    }

    @Test
    void shouldDeleteLawyer() {
        UUID lawyerId = savedLawyer.getId();
        assertThat(lawyerRepository.findById(lawyerId)).isPresent();

        lawyerRepository.deleteById(lawyerId);

        assertThat(lawyerRepository.findById(lawyerId)).isEmpty();
    }

    @Test
    void shouldVerifyTuitionNumberUniqueness() {
        Lawyer newLawyer = new Lawyer();
        newLawyer.setTuitionNumber("MP-987654");
        newLawyer.setEmail("otro@abogado.com");
        newLawyer.setPassword("pass123");

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> lawyerRepository.saveAndFlush(newLawyer)
        );
    }
}