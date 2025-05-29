package com.dbp.legalcheck.application;

import com.dbp.legalcheck.auth.domain.JwtService;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.common.enums.GuideType;
import com.dbp.legalcheck.domain.guide.Guide;
import com.dbp.legalcheck.domain.lawyer.Lawyer;
import com.dbp.legalcheck.dto.guide.GuideRequestDTO;
import com.dbp.legalcheck.infrastructure.guide.GuideRepository;
import com.dbp.legalcheck.infrastructure.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService; // Your JWT service

    private GuideRequestDTO sampleRequest;
    private UUID guideId;
    private Lawyer testLawyer;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Create test lawyer
        testLawyer = new Lawyer();
        testLawyer.setTuitionNumber("1234567890");
        testLawyer.setYearExperience(10);
        testLawyer.setEmail("abogado@example.com");
        testLawyer.setPassword("password");
        testLawyer.setFirstName("Abogado");
        testLawyer.setLastName("Test");
        testLawyer.setPhoneNumber("1234567890");
        testLawyer.setRole(UserRole.ABOGADO);
        testLawyer = userRepository.save(testLawyer);

        // Generate valid JWT token
        validToken = jwtService.generateToken(testLawyer);

        // Create sample guide
        Guide guide = new Guide();
        guide.setTitle("Guía Legal de Contratos");
        guide.setContent("Contenido de ejemplo...");
        guide.setAuthor(testLawyer);
        guide.setType(GuideType.LABORAL);
        guide = guideRepository.save(guide);
        guideId = guide.getId();

        // Create sample request DTO
        sampleRequest = new GuideRequestDTO();
        sampleRequest.setTitle("Guía Legal de Contratos");
        sampleRequest.setContent("Contenido de ejemplo...");
        sampleRequest.setType(GuideType.LABORAL);
    }

    @Test
    void publishGuideShouldReturnCreated() throws Exception {
        GuideRequestDTO newRequest = new GuideRequestDTO();
        newRequest.setTitle("Otra guía legal");
        newRequest.setContent("Contenido nuevo...");
        newRequest.setType(GuideType.LABORAL);

        mockMvc.perform(post("/api/guides")
                        .header("Authorization", "Bearer " + validToken)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ABOGADO")
    void listAllGuidesShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/guides"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ABOGADO")
    void getGuideByIdShouldReturnGuide() throws Exception {
        mockMvc.perform(get("/api/guides/{id}", guideId))
                .andExpect(status().isOk());
    }

    @Test
    void editGuideShouldUpdateGuide() throws Exception {
        sampleRequest.setContent("Nuevo contenido actualizado");

        mockMvc.perform(put("/api/guides/{id}", guideId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteGuideShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/guides/{id}", guideId)
                        .header("Authorization", "Bearer " + validToken)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}