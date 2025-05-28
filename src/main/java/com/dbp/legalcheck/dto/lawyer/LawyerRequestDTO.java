package com.dbp.legalcheck.dto.lawyer;

import java.util.List;

import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.domain.lawyer.Lawyer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LawyerRequestDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String tuitionNumber;
    private Integer yearExperience;
    @NotEmpty
    private List<LawyerSpecialization> specializations;

    public LawyerRequestDTO(Lawyer lawyer) {
        this.firstName = lawyer.getFirstName();
        this.lastName = lawyer.getLastName();
        this.email = lawyer.getEmail();
        this.phoneNumber = lawyer.getPhoneNumber();
        this.tuitionNumber = lawyer.getTuitionNumber();
        this.yearExperience = lawyer.getYearExperience();
        this.specializations = lawyer.getSpecializations();
    }

}
