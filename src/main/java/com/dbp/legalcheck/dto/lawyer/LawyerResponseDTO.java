package com.dbp.legalcheck.dto.lawyer;

import java.util.List;

import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.domain.lawyer.Lawyer;

import lombok.Data;

@Data
public class LawyerResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String tuitionNumber;
    private Integer yearExperience;
    private List<LawyerSpecialization> specializations;

    public LawyerResponseDTO(Lawyer lawyer) {
        this.firstName = lawyer.getFirstName();
        this.lastName = lawyer.getLastName();
        this.email = lawyer.getEmail();
        this.phoneNumber = lawyer.getPhoneNumber();
        this.tuitionNumber = lawyer.getTuitionNumber();
        this.yearExperience = lawyer.getYearExperience();
        this.specializations = lawyer.getSpecializations();
    }
}
