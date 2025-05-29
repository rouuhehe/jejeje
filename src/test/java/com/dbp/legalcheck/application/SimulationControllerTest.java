package com.dbp.legalcheck.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.application.simulation.SimulationController;
import com.dbp.legalcheck.common.enums.SimulationType;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.simulation.Simulation;
import com.dbp.legalcheck.domain.simulation.SimulationService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.exception.simulation.SimulationNoOwnerException;
import com.dbp.legalcheck.exception.simulation.SimulationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SimulationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SimulationService simulationService;

    @InjectMocks
    private SimulationController simulationController;

    private User testUser;
    private UUID simulationId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(simulationController).build();

        simulationId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.ABOGADO);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())
        );
    }

    @Test
    void simulateValidAlimonyRequestReturnsOk() throws Exception {
        BigDecimal salary = new BigDecimal("1000");
        int numChilds = 2;
        SimulationType type = SimulationType.ALIMONY;
        BigDecimal expectedResult = new BigDecimal("300.00");

        when(simulationService.simulate(eq(salary), eq(numChilds), eq(type), any(User.class)))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/api/simulations")
                        .param("salary", salary.toString())
                        .param("numChilds", String.valueOf(numChilds))
                        .param("simulationType", type.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult.toString()));

        verify(simulationService).simulate(eq(salary), eq(numChilds), eq(type), any(User.class));
    }

    @Test
    void simulate_InvalidSimulationType_ReturnsBadRequest() throws Exception {
        BigDecimal salary = new BigDecimal("1000");
        int numChilds = 2;
        String invalidType = "INVALID_TYPE";

        mockMvc.perform(post("/api/simulations")
                        .param("salary", salary.toString())
                        .param("numChilds", String.valueOf(numChilds))
                        .param("simulationType", invalidType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(simulationService);
    }

    @Test
    void simulate_MissingParameters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/simulations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(simulationService);
    }

    @Test
    void getSimulationHistoryAuthenticatedUserReturnsOk() throws Exception {
        Simulation simulation1 = createTestSimulation(UUID.randomUUID(), testUser);
        Simulation simulation2 = createTestSimulation(UUID.randomUUID(), testUser);
        List<Simulation> expectedSimulations = List.of(simulation1, simulation2);

        when(simulationService.getSimulationHistory(any(User.class))).thenReturn(expectedSimulations);

        mockMvc.perform(get("/api/simulations/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(simulationService).getSimulationHistory(any(User.class));
    }

    @Test
    void getSimulationValidIdAndOwnerReturnsOk() throws Exception {
        Simulation expectedSimulation = createTestSimulation(simulationId, testUser);

        when(simulationService.getSimulation(eq(simulationId), any(User.class)))
                .thenReturn(expectedSimulation);

        mockMvc.perform(get("/api/simulations/" + simulationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(simulationId.toString()));

        verify(simulationService).getSimulation(eq(simulationId), any(User.class));
    }

    @Test
    void getSimulation_NonExistingId_ReturnsNotFound() throws Exception {
        when(simulationService.getSimulation(eq(simulationId), any(User.class)))
                .thenThrow(new SimulationNotFoundException());

        mockMvc.perform(get("/api/simulations/" + simulationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(simulationService).getSimulation(eq(simulationId), any(User.class));
    }

    @Test
    void getSimulationUnauthorizedAccessReturnsForbidden() throws Exception {
        when(simulationService.getSimulation(eq(simulationId), any(User.class)))
                .thenThrow(new SimulationNoOwnerException());

        mockMvc.perform(get("/api/simulations/" + simulationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(simulationService).getSimulation(eq(simulationId), any(User.class));
    }

    private Simulation createTestSimulation(UUID id, User user) {
        Simulation simulation = new Simulation();
        simulation.setId(id);
        simulation.setUser(user);
        simulation.setType(SimulationType.ALIMONY);
        simulation.setInput("Test Input");
        simulation.setResult("Test Result");
        simulation.setCreatedAt(Instant.now());
        return simulation;
    }
}