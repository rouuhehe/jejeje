package com.dbp.legalcheck.domain.simulator;

public interface Simulator<I, O> {
    O simulate(I input);
}
