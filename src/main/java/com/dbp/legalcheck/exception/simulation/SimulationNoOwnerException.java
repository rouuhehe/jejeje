package com.dbp.legalcheck.exception.simulation;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class SimulationNoOwnerException extends UnauthorizedException {
    public SimulationNoOwnerException() {
        super("User is not the simulation owner");
    }
}
