package com.dbp.legalcheck.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.dbp.legalcheck.common.enums.SimulationType;
import com.dbp.legalcheck.domain.simulation.Simulation;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.infrastructure.simulation.SimulationRepository;
import com.dbp.legalcheck.infrastructure.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SimulationRepositoryTest {

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("repo_test@example.com");
        user.setPassword("{noop}password");
        user = userRepository.save(user);
    }

    @Test
    void shouldSaveAndFindByUser() {
        Simulation sim = new Simulation();
        sim.setUser(user);
        sim.setType(SimulationType.ALIMONY);
        sim.setInput("¿Tengo derecho a pensión?");
        sim.setResult("Sí, cumples los requisitos.");

        simulationRepository.save(sim);

        List<Simulation> found = simulationRepository.findAllByUser(user);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getType()).isEqualTo(SimulationType.ALIMONY);
        assertThat(found.get(0).getInput()).contains("derecho");
    }
}
