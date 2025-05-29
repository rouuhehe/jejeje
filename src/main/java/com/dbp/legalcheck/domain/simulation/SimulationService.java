package com.dbp.legalcheck.domain.simulation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.SimulationType;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.simulator.strategy.AlimonySimulator;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.simulator.alimonySimulator.AlimonyInput;
import com.dbp.legalcheck.exception.simulation.AmbiguousSimulatorException;
import com.dbp.legalcheck.exception.simulation.SimulationNoOwnerException;
import com.dbp.legalcheck.exception.simulation.SimulationNotFoundException;
import com.dbp.legalcheck.infrastructure.simulation.SimulationRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimulationService {
    private final SimulationRepository simulationRepository;

    // POST /api/simulations/
    public BigDecimal simulate(BigDecimal salary, Integer numChilds, SimulationType simulationType, User currentUser) {
        Simulation simulation = new Simulation();
        simulation.setUser(currentUser);
        simulation.setType(simulationType);

        switch (simulationType) {
            case ALIMONY:

                AlimonyInput input = new AlimonyInput(salary, numChilds);
                AlimonySimulator alimonySimulator = new AlimonySimulator();
                BigDecimal result = alimonySimulator.simulate(input);

                simulation.setInput(input.toString());
                simulation.setResult(result.toString());

                simulationRepository.save(simulation);

                return result;
            default:
                throw new AmbiguousSimulatorException();
        }
    }

    // GET /api/simulations/history
    public List<Simulation> getSimulationHistory(User currentUser) {
        return simulationRepository.findAllByUser(currentUser);
    }

    // GET /api/simulations/{id}
    public Simulation getSimulation(UUID id, User currentUser) {
        Simulation simulation = simulationRepository.findById(id)
                .orElseThrow(SimulationNotFoundException::new);

        if (currentUser.getRole() == UserRole.ADMINISTRADOR) {
            return simulation;
        }

        if (!simulation.getUser().getId().equals(currentUser.getId())) {
            throw new SimulationNoOwnerException();
        }

        return simulation;
    }
}
