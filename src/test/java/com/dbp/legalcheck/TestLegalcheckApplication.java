package com.dbp.legalcheck;

import org.springframework.boot.SpringApplication;

public class TestLegalcheckApplication {

    public static void main(String[] args) {
        SpringApplication.from(LegalcheckApplication::main).with(PostgresTestContainersConfiguration.class).run(args);
    }

}
