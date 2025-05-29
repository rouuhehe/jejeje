package com.dbp.legalcheck.exception.simulation;

import com.dbp.legalcheck.common.exception.ResourceNotFoundException;

public class SimulationNotFoundException extends ResourceNotFoundException {
    public SimulationNotFoundException() {
        super("Simulation could not be found");
    }
}
