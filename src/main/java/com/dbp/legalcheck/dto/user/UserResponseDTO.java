package com.dbp.legalcheck.dto.user;

import java.time.Instant;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.UserRole;

import lombok.Data;

@Data
public class UserResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private boolean verified;
    private Instant createdAt;
}
