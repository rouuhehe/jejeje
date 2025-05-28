package com.dbp.legalcheck.domain.guide;

import com.dbp.legalcheck.common.enums.GuideType;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.lawyer.Lawyer;
import com.dbp.legalcheck.domain.lawyer.LawyerService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.guide.GuideReponseDTO;
import com.dbp.legalcheck.dto.guide.GuideRequestDTO;
import com.dbp.legalcheck.exception.guide.GuideAlreadyExistsException;
import com.dbp.legalcheck.exception.guide.GuideNotFoundException;
import com.dbp.legalcheck.exception.guide.UserNotAuthorException;
import com.dbp.legalcheck.infrastructure.guide.GuideRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuideServiceTest {

    private GuideRepository guideRepository;
    private LawyerService lawyerService;
    private ModelMapper modelMapper;
    private GuideService guideService;

    @BeforeEach
    void setUp() {
        guideRepository = mock(GuideRepository.class);
        lawyerService = mock(LawyerService.class);
        modelMapper = new ModelMapper();
        guideService = new GuideService(guideRepository, lawyerService, modelMapper);
    }

    @Test
    void listAllGuidesShouldReturnListOfDTOs() {
        Guide guide = new Guide();
        guide.setTitle("Test Guide");
        guide.setContent("Contenido");
        guide.setAuthor(new Lawyer());

        when(guideRepository.findAll()).thenReturn(List.of(guide));

        List<GuideReponseDTO> result = guideService.listAllGuides();

        assertEquals(1, result.size());
        assertEquals("Test Guide", result.get(0).getTitle());
    }

    @Test
    void getGuideByIdWhenFoundShouldReturnDTO() {
        UUID id = UUID.randomUUID();
        Guide guide = new Guide();
        guide.setId(id);
        guide.setTitle("Test");
        guide.setContent("Content");
        guide.setAuthor(new Lawyer());

        when(guideRepository.findById(id)).thenReturn(Optional.of(guide));

        GuideReponseDTO dto = guideService.getGuideById(id);

        assertEquals("Test", dto.getTitle());
    }

    @Test
    void getGuideByIdWhenNotFoundShouldThrow() {
        UUID id = UUID.randomUUID();
        when(guideRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(GuideNotFoundException.class, () -> guideService.getGuideById(id));
    }

    @Test
    void publishGuideWithValidDataShouldReturnDTO() {
        GuideRequestDTO request = new GuideRequestDTO();
        request.setTitle("Nueva Guía");
        request.setContent("Contenido");
        request.setType(GuideType.PENAL);

        User user = new User();
        user.setRole(UserRole.ABOGADO);
        user.setId(UUID.randomUUID());

        Lawyer lawyer = new Lawyer();
        lawyer.setId(user.getId());

        when(guideRepository.existsByTitle("Nueva Guía")).thenReturn(false);
        when(lawyerService.getLawyerFromUser(user)).thenReturn(lawyer);

        GuideReponseDTO dto = guideService.publishGuide(request, user);

        assertEquals("Nueva Guía", dto.getTitle());
    }

    @Test
    void publishGuideWithDuplicateTitleShouldThrow() {
        GuideRequestDTO request = new GuideRequestDTO();
        request.setTitle("Repetida");

        User user = new User();
        user.setRole(UserRole.ABOGADO);

        when(guideRepository.existsByTitle("Repetida")).thenReturn(true);

        assertThrows(GuideAlreadyExistsException.class, () -> guideService.publishGuide(request, user));
    }

    @Test
    void editGuideWithValidDataShouldUpdateAndReturnDTO() {
        UUID id = UUID.randomUUID();

        Guide existing = new Guide();
        existing.setId(id);
        existing.setTitle("Viejo título");
        Lawyer author = new Lawyer();
        author.setId(UUID.randomUUID());
        existing.setAuthor(author);

        GuideRequestDTO request = new GuideRequestDTO();
        request.setTitle("Nuevo título");
        request.setContent("Nuevo contenido");
        request.setType(GuideType.LABORAL);

        User user = new User();
        user.setId(author.getId());
        user.setRole(UserRole.ABOGADO);

        when(guideRepository.findById(id)).thenReturn(Optional.of(existing));
        when(guideRepository.save(any())).thenReturn(existing);

        GuideReponseDTO dto = guideService.editGuide(id, request, user);

        assertEquals("Nuevo título", dto.getTitle());
    }

    @Test
    void editGuideWithUnauthorizedUserShouldThrow() {
        UUID id = UUID.randomUUID();
        Guide guide = new Guide();
        guide.setId(id);
        Lawyer author = new Lawyer();
        author.setId(UUID.randomUUID());
        guide.setAuthor(author);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.ABOGADO); // Not admin, not author

        GuideRequestDTO request = new GuideRequestDTO();

        when(guideRepository.findById(id)).thenReturn(Optional.of(guide));

        assertThrows(UserNotAuthorException.class, () -> guideService.editGuide(id, request, user));
    }
}
