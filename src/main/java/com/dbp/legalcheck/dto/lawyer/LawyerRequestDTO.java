package com.dbp.legalcheck.dto.lawyer;

import java.util.List;

import com.dbp.legalcheck.common.enums.LawyerSpecialization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LawyerRequestDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String tuitionNumber;
    private Integer yearExperience;
    @NotEmpty
    private List<LawyerSpecialization> specializations;
}
