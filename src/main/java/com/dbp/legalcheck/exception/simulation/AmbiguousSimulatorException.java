package com.dbp.legalcheck.exception.simulation;

import com.dbp.legalcheck.common.exception.InvalidOperationException;

public class AmbiguousSimulatorException extends InvalidOperationException {
    public AmbiguousSimulatorException() {
        super("Simulator call is ambiguous");
    }
}
