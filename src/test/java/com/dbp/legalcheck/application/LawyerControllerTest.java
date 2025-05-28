package com.dbp.legalcheck.application;

import com.dbp.legalcheck.PostgresTestContainersConfiguration;
import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.domain.lawyer.Lawyer;
import com.dbp.legalcheck.dto.lawyer.LawyerRequestDTO;
import com.dbp.legalcheck.infrastructure.lawyer.LawyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

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

    @BeforeEach
    public void setUp() {
        lawyerRepository.deleteAll();
    }

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
        lawyer2.setFirstName("Alan");
        lawyer2.setLastName("Turing");
        lawyer2.setEmail("aturing@enigma.org");
        lawyer2.setPassword("codebreaker");
        lawyer2.setPhoneNumber("933111444");
        lawyer2.setTuitionNumber("C345678");
        lawyer2.setYearExperience(10);
        lawyer2.setSpecializations(List.of(LawyerSpecialization.DERECHO_PENAL));
        lawyerRepository.save(lawyer2);

        mockMvc.perform(get("/api/lawyers").with(user("testusuario").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.email == '" + lawyer1.getEmail() + "')]").exists())
                .andExpect(jsonPath("$[?(@.email == '" + lawyer2.getEmail() + "')]").exists());
    }

    // Test de getLawyerById exitoso
    @Test
    public void shouldReturnLawyer_WhenGetLawyerById() throws Exception {
        Lawyer lawyer = new Lawyer();
        UUID id = UUID.randomUUID();
        lawyer.setId(id);
        lawyer.setFirstName("Gepeto");
        lawyer.setLastName("OpenAi");
        lawyer.setEmail("gepeto.ai@email.com");
        lawyer.setPassword("computing123");
        lawyer.setPhoneNumber("912345678");
        lawyer.setTuitionNumber("D987654");
        lawyer.setYearExperience(12);
        lawyer.setSpecializations(List.of(LawyerSpecialization.DERECHO_CONSTITUCIONAL));
        lawyerRepository.save(lawyer);

        mockMvc.perform(get("/api/lawyers/{id}", id).with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(lawyer.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(lawyer.getLastName()))
                .andExpect(jsonPath("$.email").value(lawyer.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(lawyer.getPhoneNumber()))
                .andExpect(jsonPath("$.tuitionNumber").value(lawyer.getTuitionNumber()))
                .andExpect(jsonPath("$.yearExperience").value(lawyer.getYearExperience()))
                .andExpect(jsonPath("$.specializations[0]").value(lawyer.getSpecializations().get(0).name()));
    }

    // Test de getLawyerById not found
    @Test
    public void shouldReturnNotFound_WhenLawyerDoesNotExist() throws Exception {
        UUID nonExistingId = UUID.randomUUID();

        mockMvc.perform(get("/api/lawyers/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Lawyer not found")));
    }

    // Test de registerLawyer
    @Test
    public void shouldRegisterLawyer_WhenRegisterLawyer() throws Exception {
        LawyerRequestDTO request = new LawyerRequestDTO();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail("ada.lovelace@legal.com");
        request.setPhoneNumber("911223344");
        request.setTuitionNumber("T999999");
        request.setYearExperience(15);
        request.setSpecializations(List.of(LawyerSpecialization.DERECHO_CONSTITUCIONAL, LawyerSpecialization.DERECHO_PENAL));

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/api/lawyers")
                        .with(user("testadmin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(request.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(request.getLastName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(request.getPhoneNumber()))
                .andExpect(jsonPath("$.tuitionNumber").value(request.getTuitionNumber()))
                .andExpect(jsonPath("$.yearExperience").value(request.getYearExperience()))
                .andExpect(jsonPath("$.specializations", hasSize(request.getSpecializations().size())));
    }

    // Test de updateLawyer
    @Test
    public void shouldUpdateLawyer_WhenUpdateLawyer() throws Exception {
        UUID id = UUID.randomUUID();
        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);
        lawyer.setFirstName("Gepeto");
        lawyer.setLastName("OpenAi");
        lawyer.setEmail("gepeto.ai@email.com");
        lawyer.setPassword("computing123");
        lawyer.setPhoneNumber("912345678");
        lawyer.setTuitionNumber("D987654");
        lawyer.setYearExperience(12);
        lawyer.setSpecializations(List.of(LawyerSpecialization.DERECHO_CONSTITUCIONAL));
        lawyerRepository.save(lawyer);

        LawyerRequestDTO updatedLawyer = new LawyerRequestDTO();
        updatedLawyer.setFirstName("UpdatedName");
        updatedLawyer.setLastName("UpdatedLast");
        updatedLawyer.setEmail("updated@email.com");
        updatedLawyer.setPhoneNumber("987654321");
        updatedLawyer.setTuitionNumber("X123456");
        updatedLawyer.setYearExperience(15);
        updatedLawyer.setSpecializations(List.of(LawyerSpecialization.DERECHO_PENAL));

        mockMvc.perform(put("/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("testlawyer").roles("LAWYER"))
                        .content(new ObjectMapper().writeValueAsString(updatedLawyer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedName"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    // Test de deleteLawyer
    @Test
    public void shouldDeleteLawyer_WhenDeleteLawyer() throws Exception {
        UUID id = UUID.randomUUID();
        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);
        lawyer.setFirstName("Juan");
        lawyer.setLastName("Bodoque");
        lawyer.setEmail("conejorojo@gmail.com");
        lawyer.setPassword("tormentachina");
        lawyer.setPhoneNumber("918937111");
        lawyer.setTuitionNumber("DEL1234");
        lawyer.setYearExperience(5);
        lawyer.setSpecializations(List.of(LawyerSpecialization.DERECHO_AGRARIO));
        lawyerRepository.save(lawyer);

        mockMvc.perform(delete("/" + id)
                        .with(user("adminuser").roles("ADMIN")))
                .andExpect(status().isNoContent());

        assertFalse(lawyerRepository.findById(id).isPresent());
    }

}
