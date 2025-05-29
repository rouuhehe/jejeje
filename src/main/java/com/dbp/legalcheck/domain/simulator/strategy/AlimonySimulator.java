package com.dbp.legalcheck.domain.simulator.strategy;

import java.math.BigDecimal;

import com.dbp.legalcheck.domain.simulator.Simulator;
import com.dbp.legalcheck.dto.simulator.alimonySimulator.AlimonyInput;

import org.springframework.stereotype.Component;

@Component
public class AlimonySimulator implements Simulator<AlimonyInput, BigDecimal> {
    @Override
    public BigDecimal simulate(AlimonyInput input) {
        BigDecimal porcentaje;
        switch (input.getNumChilds()) {
            case 1 -> porcentaje = new BigDecimal("0.20");
            case 2 -> porcentaje = new BigDecimal("0.40");
            default -> porcentaje = new BigDecimal("0.60");
        }

        return input.getSalary().multiply(porcentaje);
    }
}
