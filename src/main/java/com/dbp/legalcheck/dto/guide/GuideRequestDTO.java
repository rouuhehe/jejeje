package com.dbp.legalcheck.dto.guide;

import com.dbp.legalcheck.common.enums.GuideType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuideRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private GuideType type;
}
