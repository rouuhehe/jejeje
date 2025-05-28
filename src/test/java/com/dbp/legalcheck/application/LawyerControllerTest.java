package com.dbp.legalcheck.application;

import com.dbp.legalcheck.PostgresTestContainersConfiguration;
import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.domain.lawyer.Lawyer;
import com.dbp.legalcheck.infrastructure.lawyer.LawyerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(PostgresTestContainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LawyerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LawyerRepository lawyerRepository;

    // Test de listLawyers
    @Test
    public void shouldReturnAllLawyers_WhenListLawyers() throws Exception {
        Lawyer lawyer1 = new Lawyer();
        lawyer1.setId(UUID.randomUUID());
        lawyer1.setFirstName("Nelson");
        lawyer1.setLastName("Mandela");
        lawyer1.setEmail("nmandela@gmail.com");
        lawyer1.setPassword("nmealnsdoenela");
        lawyer1.setPhoneNumber("912123123");
        lawyer1.setTuitionNumber("A123456");
        lawyer1.setYearExperience(20);
        lawyer1.setSpecializations(List.of(LawyerSpecialization.DERECHO_PENAL,LawyerSpecialization.DERECHO_CONSTITUCIONAL));
        lawyerRepository.save(lawyer1);

        Lawyer lawyer2 = new Lawyer();
        lawyer2.setId(UUID.randomUUID());
        lawyer2.setFirstName("Rosa");
        lawyer2.setLastName("Luxemburgo");
        lawyer2.setEmail("rosa.lux@gmail.com");
        lawyer2.setPassword("rosarules");
        lawyer2.setPhoneNumber("911222333");
        lawyer2.setTuitionNumber("B789012");
        lawyer2.setYearExperience(15);
        lawyer2.setSpecializations(List.of(LawyerSpecialization.DERECHO_LABORAL, LawyerSpecialization.DERECHO_CONSTITUCIONAL));
        lawyerRepository.save(lawyer2);

        Lawyer lawyer3 = new Lawyer();
        lawyer3.setId(UUID.randomUUID());
        lawyer3.setFirstName("Alan");
        lawyer3.setLastName("Turing");
        lawyer3.setEmail("aturing@enigma.org");
        lawyer3.setPassword("codebreaker");
        lawyer3.setPhoneNumber("933111444");
        lawyer3.setTuitionNumber("C345678");
        lawyer3.setYearExperience(10);
        lawyer3.setSpecializations(List.of(LawyerSpecialization.DERECHO_PENAL));
        lawyerRepository.save(lawyer3);

    }

    // Test de getLawyerById
    @Test
    public void shouldReturnLawyer_WhenGetLawyerById() throws Exception {

    }

    // Test de registerLawyer
    @Test
    public void shouldRegisterLawyer_WhenRegisterLawyer() throws Exception {

    }

    // Test de updateLawyer
    @Test
    public void shouldUpdateLawyer_WhenUpdateLawyer() throws Exception {

    }

    // Test de deleteLawyer
    @Test
    public void shouldDeleteLawyer_WhenDeleteLawyer() throws Exception {

    }

}
