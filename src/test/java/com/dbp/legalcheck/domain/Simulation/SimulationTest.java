package com.dbp.legalcheck.domain.Simulation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.SimulationType;
import com.dbp.legalcheck.domain.simulation.Simulation;
import com.dbp.legalcheck.domain.user.User;

import org.junit.jupiter.api.Test;

class SimulationTest {

    @Test
    void simulationShouldHoldDataCorrectly() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setEmail("user@example.com");

        Simulation simulation = new Simulation();
        simulation.setId(id);
        simulation.setUser(user);
        simulation.setType(SimulationType.ALIMONY);
        simulation.setInput("¿Me corresponde pensión?");
        simulation.setResult("Sí, si cumples con los requisitos legales.");
        Instant now = Instant.now();
        simulation.setCreatedAt(now);

        assertThat(simulation.getId()).isEqualTo(id);
        assertThat(simulation.getUser().getEmail()).isEqualTo("user@example.com");
        assertThat(simulation.getType()).isEqualTo(SimulationType.ALIMONY);
        assertThat(simulation.getInput()).isEqualTo("¿Me corresponde pensión?");
        assertThat(simulation.getResult()).isEqualTo("Sí, si cumples con los requisitos legales.");
        assertThat(simulation.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void simulationToStringShouldIncludeInputAndResult() {
        Simulation sim = new Simulation();
        sim.setInput("Entrada de prueba");
        sim.setResult("Resultado de prueba");

        String toString = sim.toString();
        assertThat(toString).contains("Entrada de prueba", "Resultado de prueba");
    }
}
