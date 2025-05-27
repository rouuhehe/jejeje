package com.dbp.legalcheck.application.lawyer;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.domain.lawyer.LawyerService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.lawyer.LawyerRequestDTO;
import com.dbp.legalcheck.dto.lawyer.LawyerResponseDTO;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lawyers")
@RequiredArgsConstructor
public class LawyerController {
    private final LawyerService lawyerService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<LawyerResponseDTO> listLawyers() {
        return lawyerService.listLawyers();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public LawyerResponseDTO getLawyerById(@PathVariable UUID id) {
        return lawyerService.getLawyerById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LawyerResponseDTO registerLawyer(@Valid @RequestBody LawyerRequestDTO lawyerDTO) {
        return lawyerService.registerLawyer(lawyerDTO);
    }

    @PreAuthorize("hasRole('LAWYER')")
    @PutMapping("/{id}")
    public LawyerResponseDTO updateLawyer(@PathVariable UUID id, @Valid @RequestBody LawyerRequestDTO lawyerDTO,
            @AuthenticationPrincipal User user) {
        return lawyerService.updateLawyer(id, lawyerDTO, user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLawyer(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        lawyerService.deleteLawyer(id, user);
    }
}
