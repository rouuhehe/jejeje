package com.dbp.legalcheck.domain.lawyer;

import java.util.List;

import com.dbp.legalcheck.common.enums.LawyerSpecialization;
import com.dbp.legalcheck.domain.user.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Data
public class Lawyer extends User {
    @Column(nullable = false, unique = true)
    private String tuitionNumber;

    @Column(nullable = false)
    private Integer yearExperience;

    @ElementCollection(targetClass = LawyerSpecialization.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "LawyerSpecializations", joinColumns = @JoinColumn(name = "lawyer_id"))
    @Column(name = "specialization")
    private List<LawyerSpecialization> specializations;
}
