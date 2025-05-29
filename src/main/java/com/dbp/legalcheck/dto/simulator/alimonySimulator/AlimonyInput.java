package com.dbp.legalcheck.dto.simulator.alimonySimulator;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlimonyInput {
    private BigDecimal salary;
    private Integer numChilds;
}
