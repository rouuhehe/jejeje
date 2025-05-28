package com.dbp.legalcheck.domain.lawyer;

import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.common.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LawyerTest {
    @Test
    void shouldCreateLawyerAndCheckFields() {
        Lawyer lawyer = new Lawyer();
        lawyer.setFirstName("Valeria");
        lawyer.setLastName("Gómez");
        lawyer.setEmail("valeria@legal.com");
        lawyer.setPassword("securepass123");
        lawyer.setTuitionNumber("T-34567");
        lawyer.setYearExperience(8);
        lawyer.setRole(UserRole.ABOGADO);
        lawyer.setSpecializations(List.of(
                LawyerSpecialization.DERECHO_CIVIL,
                LawyerSpecialization.DERECHO_PENAL
        ));

        assertThat(lawyer.getFirstName()).isEqualTo("Valeria");
        assertThat(lawyer.getTuitionNumber()).isEqualTo("T-34567");
        assertThat(lawyer.getYearExperience()).isEqualTo(8);
        assertThat(lawyer.getSpecializations()).containsExactly(
                LawyerSpecialization.DERECHO_CIVIL, LawyerSpecialization.DERECHO_PENAL
        );
        assertThat(lawyer.getRole()).isEqualTo(UserRole.ABOGADO);
    }
}
