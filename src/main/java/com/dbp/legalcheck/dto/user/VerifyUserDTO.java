package com.dbp.legalcheck.dto.user;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyUserDTO {
    @NotNull
    private UUID verificationId;
}
