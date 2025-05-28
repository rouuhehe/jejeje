package com.dbp.legalcheck.application.guide;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.domain.guide.GuideService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.guide.GuideReponseDTO;
import com.dbp.legalcheck.dto.guide.GuideRequestDTO;

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
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {
    private final GuideService guideService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<GuideReponseDTO> listAllGuides() {
        return guideService.listAllGuides();
    }

    @PreAuthorize("hasRole('ABOGADO')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuideReponseDTO publishGuide(@Valid @RequestBody GuideRequestDTO guideDTO,
            @AuthenticationPrincipal User user) {
        return guideService.publishGuide(guideDTO, user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public GuideReponseDTO getGuideById(@PathVariable UUID id) {
        return guideService.getGuideById(id);
    }

    @PreAuthorize("hasRole('ABOGADO')")
    @PutMapping("/{id}")
    public GuideReponseDTO editGuide(@PathVariable UUID id, @Valid @RequestBody GuideRequestDTO guideDTO,
            @AuthenticationPrincipal User user) {
        return guideService.editGuide(id, guideDTO, user);
    }

    @PreAuthorize("hasRole('ABOGADO')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGuide(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        guideService.deleteGuide(id, user);
    }
}
