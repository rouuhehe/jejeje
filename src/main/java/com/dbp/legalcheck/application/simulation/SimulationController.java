package com.dbp.legalcheck.application.simulation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.SimulationType;
import com.dbp.legalcheck.domain.simulation.Simulation;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.domain.simulation.SimulationService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final SimulationService simulationService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public BigDecimal simulate(@RequestParam BigDecimal salary, @RequestParam Integer numChilds,
            @RequestParam SimulationType simulationType, @AuthenticationPrincipal User user) {
        return simulationService.simulate(salary, numChilds, simulationType, user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/history")
    public List<Simulation> getSimulationHistory(@AuthenticationPrincipal User user) {
        return simulationService.getSimulationHistory(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public Simulation getSimulation(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return simulationService.getSimulation(id, user);
    }
}
