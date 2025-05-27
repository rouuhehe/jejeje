package com.dbp.legalcheck.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class VerifyResendDTO {
    @Email
    private String email;
}
