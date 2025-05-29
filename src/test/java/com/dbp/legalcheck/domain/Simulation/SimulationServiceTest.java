package com.dbp.legalcheck.domain.Simulation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.SimulationType;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.simulation.Simulation;
import com.dbp.legalcheck.domain.simulation.SimulationService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.simulator.alimonySimulator.AlimonyInput;
import com.dbp.legalcheck.exception.simulation.AmbiguousSimulatorException;
import com.dbp.legalcheck.exception.simulation.SimulationNoOwnerException;
import com.dbp.legalcheck.exception.simulation.SimulationNotFoundException;
import com.dbp.legalcheck.infrastructure.simulation.SimulationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private SimulationRepository simulationRepository;

    @InjectMocks
    private SimulationService simulationService;

    private User regularUser;
    private User adminUser;
    private UUID simulationId;

    @BeforeEach
    void setUp() {
        simulationId = UUID.randomUUID();
        regularUser = new User();
        regularUser.setId(UUID.randomUUID());
        regularUser.setRole(UserRole.ABOGADO);

        adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setRole(UserRole.ADMINISTRADOR);
    }

    @Test
    void simulateAlimonyTypeReturnsCorrectResultAndSavesSimulation() {
        // Arrange
        BigDecimal salary = new BigDecimal("1000");
        int numChilds = 2;
        SimulationType type = SimulationType.ALIMONY;

        Simulation expectedSimulation = new Simulation();
        expectedSimulation.setUser(regularUser);
        expectedSimulation.setType(type);
        expectedSimulation.setInput(new AlimonyInput(salary, numChilds).toString());

        when(simulationRepository.save(any(Simulation.class))).thenAnswer(invocation -> {
            Simulation s = invocation.getArgument(0);
            s.setId(simulationId);
            s.setCreatedAt(Instant.now());
            return s;
        });

        // Act
        BigDecimal result = simulationService.simulate(salary, numChilds, type, regularUser);

        // Assert
        assertNotNull(result);
        verify(simulationRepository).save(any(Simulation.class));
    }

    @Test
    void simulateUnknownTypeThrowsAmbiguousSimulatorException() {

        BigDecimal salary = new BigDecimal("1000");
        int numChilds = 2;
        SimulationType type = SimulationType.OTHER;

        assertThrows(AmbiguousSimulatorException.class, () -> {
            simulationService.simulate(salary, numChilds, type, regularUser);
        });
    }

    @Test
    void getSimulationHistoryReturnsUserSimulations() {
        Simulation simulation1 = new Simulation();
        simulation1.setId(UUID.randomUUID());
        simulation1.setUser(regularUser);

        Simulation simulation2 = new Simulation();
        simulation2.setId(UUID.randomUUID());
        simulation2.setUser(regularUser);

        List<Simulation> expectedSimulations = List.of(simulation1, simulation2);

        when(simulationRepository.findAllByUser(regularUser)).thenReturn(expectedSimulations);

        List<Simulation> result = simulationService.getSimulationHistory(regularUser);

        assertEquals(2, result.size());
        verify(simulationRepository).findAllByUser(regularUser);
    }

    @Test
    void getSimulationExistingIdAndOwnerReturnsSimulation() {
        Simulation expectedSimulation = new Simulation();
        expectedSimulation.setId(simulationId);
        expectedSimulation.setUser(regularUser);

        when(simulationRepository.findById(simulationId)).thenReturn(Optional.of(expectedSimulation));

        Simulation result = simulationService.getSimulation(simulationId, regularUser);

        assertNotNull(result);
        assertEquals(simulationId, result.getId());
    }

    @Test
    void getSimulationExistingIdAndAdminReturnsSimulation() {
        Simulation expectedSimulation = new Simulation();
        expectedSimulation.setId(simulationId);
        expectedSimulation.setUser(regularUser); // Different user

        when(simulationRepository.findById(simulationId)).thenReturn(Optional.of(expectedSimulation));

        Simulation result = simulationService.getSimulation(simulationId, adminUser);

        assertNotNull(result);
        assertEquals(simulationId, result.getId());
    }

    @Test
    void getSimulationNonExistingIdThrowsSimulationNotFoundException() {
        when(simulationRepository.findById(simulationId)).thenReturn(Optional.empty());

        assertThrows(SimulationNotFoundException.class, () -> {
            simulationService.getSimulation(simulationId, regularUser);
        });
    }

    @Test
    void getSimulationExistingIdButNotOwnerThrowsSimulationNoOwnerException() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setRole(UserRole.ABOGADO);

        Simulation simulation = new Simulation();
        simulation.setId(simulationId);
        simulation.setUser(anotherUser);

        when(simulationRepository.findById(simulationId)).thenReturn(Optional.of(simulation));

        assertThrows(SimulationNoOwnerException.class, () -> {
            simulationService.getSimulation(simulationId, regularUser);
        });
    }
}