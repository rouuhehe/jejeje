package com.dbp.legalcheck.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLoginDTO {
    @Email
    private String email;
    @NotBlank
    private String password;
}
