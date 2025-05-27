package com.dbp.legalcheck.domain.guide;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.lawyer.LawyerService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.guide.GuideReponseDTO;
import com.dbp.legalcheck.dto.guide.GuideRequestDTO;
import com.dbp.legalcheck.exception.guide.GuideAlreadyExistsException;
import com.dbp.legalcheck.exception.guide.GuideNotFoundException;
import com.dbp.legalcheck.exception.guide.InsufficientPermissionsException;
import com.dbp.legalcheck.exception.guide.UserNotAuthorException;
import com.dbp.legalcheck.infrastructure.guide.GuideRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideService {
    private final GuideRepository guideRepository;
    private final LawyerService lawyerService;
    private final ModelMapper modelMapper;

    // GET /api/guides
    public List<GuideReponseDTO> listAllGuides() {
        return guideRepository.findAll().stream()
                .map(GuideReponseDTO::new).toList();
    }

    // GET /api/guides/{id}
    public GuideReponseDTO getGuideById(UUID id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(GuideNotFoundException::new);

        return modelMapper.map(guide, GuideReponseDTO.class);
    }

    // POST /api/guides
    public GuideReponseDTO publishGuide(GuideRequestDTO request, User user) {
        if (user.getRole().equals(UserRole.USUARIO) ||
                guideRepository.existsByTitle(request.getTitle())) {
            throw new GuideAlreadyExistsException();
        }

        Guide newGuide = modelMapper.map(request, Guide.class);
        newGuide.setAuthor(
                lawyerService.getLawyerFromUser(user));

        return new GuideReponseDTO(newGuide);
    }

    // PUT /api/guides/{id}
    public GuideReponseDTO editGuide(UUID id, GuideRequestDTO request, User user) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(GuideNotFoundException::new);

        if (!guide.getAuthor().getId().equals(user.getId()) &&
                !user.getRole().equals(UserRole.ADMINISTRADOR)) {
            throw new UserNotAuthorException();
        }

        modelMapper.map(request, guide);
        return new GuideReponseDTO(guideRepository.save(guide));
    }

    // DELETE /api/guides/{id}
    public void deleteGuide(UUID id, User user) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(GuideNotFoundException::new);

        if (!user.getRole().equals(UserRole.ADMINISTRADOR) &&
                !guide.getAuthor().getId().equals(user.getId())) {
            throw new InsufficientPermissionsException();
        }

        guideRepository.delete(guide);
    }
}
