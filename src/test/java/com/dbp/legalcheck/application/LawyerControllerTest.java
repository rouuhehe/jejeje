package com.dbp.legalcheck.application;

import com.dbp.legalcheck.application.lawyer.LawyerController;
import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.lawyer.LawyerService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.lawyer.LawyerRequestDTO;
import com.dbp.legalcheck.dto.lawyer.LawyerResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@WebMvcTest(LawyerController.class)
class LawyerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LawyerService lawyerService;

    @org.springframework.boot.test.context.TestConfiguration
    static class MockConfig {
        @org.springframework.context.annotation.Bean
        public LawyerService lawyerService() {
            return org.mockito.Mockito.mock(LawyerService.class);
        }
        @Bean
        public com.dbp.legalcheck.auth.domain.JwtService jwtService() {
            return mock(com.dbp.legalcheck.auth.domain.JwtService.class);
        }

        @Bean
        public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
            return username -> {
                User user = new User();
                user.setId(UUID.randomUUID());
                user.setEmail("admin@example.com");
                user.setRole(UserRole.ADMINISTRADOR);
                return org.springframework.security.core.userdetails.User
                        .withUsername("admin@example.com")
                        .password("password")
                        .roles("ADMINISTRADOR")
                        .build();
            };
        }

    }

    private LawyerRequestDTO sampleRequest;
    private LawyerResponseDTO sampleResponse;
    private UUID sampleId;

    @BeforeEach
    void setup() {
        sampleId = UUID.randomUUID();

        sampleRequest = new LawyerRequestDTO();
        sampleRequest.setFirstName("Ana");
        sampleRequest.setLastName("Soto");
        sampleRequest.setEmail("ana@example.com");
        sampleRequest.setPassword("123456");
        sampleRequest.setPhoneNumber("123456789");
        sampleRequest.setTuitionNumber("XYZ987");
        sampleRequest.setYearExperience(5);
        sampleRequest.setSpecializations(List.of(LawyerSpecialization.DERECHO_CIVIL));

        sampleResponse = new LawyerResponseDTO();
        sampleResponse.setFirstName("Ana");
        sampleResponse.setLastName("Soto");
    }
    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMINISTRADOR")
    void registerLawyerShouldReturnCreatedLawyer() throws Exception {
        Mockito.doReturn(sampleResponse).when(lawyerService).registerLawyer(any());

        mockMvc.perform(post("/api/lawyers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @WithMockUser
    void listLawyersShouldReturnList() throws Exception {
        Mockito.doReturn(List.of(sampleResponse)).when(lawyerService).listLawyers();

        mockMvc.perform(get("/api/lawyers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void getLawyerByIdShouldReturnLawyer() throws Exception {
        Mockito.doReturn(sampleResponse).when(lawyerService).getLawyerById(sampleId);

        mockMvc.perform(get("/api/lawyers/{id}", sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @WithMockUser(roles = "ABOGADO")
    void updateLawyerShouldUpdateAndReturn() throws Exception {
        User user = new User();
        user.setId(sampleId);
        user.setEmail("abogado@example.com");
        user.setRole(UserRole.ABOGADO);

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_ABOGADO")));

        Mockito.doReturn(sampleResponse).when(lawyerService).updateLawyer(eq(sampleId), any(), any());

        mockMvc.perform(put("/api/lawyers/{id}", sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest))
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMINISTRADOR")
    void deleteLawyerShouldReturnNoContent() throws Exception {

        User user = new User();
        user.setId(sampleId);
        user.setEmail("admin@example.com");
        user.setRole(UserRole.ADMINISTRADOR);

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ADMINISTRADOR")));

        Mockito.doNothing().when(lawyerService).deleteLawyer(eq(sampleId), any());

        mockMvc.perform(delete("/api/lawyers/{id}", sampleId)
                        .with(authentication(auth)))
                .andExpect(status().isNoContent());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

}
