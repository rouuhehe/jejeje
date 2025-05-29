package com.dbp.legalcheck.infrastructure.simulation;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.domain.simulation.Simulation;
import com.dbp.legalcheck.domain.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationRepository extends JpaRepository<Simulation, UUID> {
    List<Simulation> findAllByUser(User user);
}
