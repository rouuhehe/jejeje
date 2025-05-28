package com.dbp.legalcheck.domain.lawyer;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.dto.lawyer.LawyerRequestDTO;
import com.dbp.legalcheck.dto.lawyer.LawyerResponseDTO;
import com.dbp.legalcheck.exception.lawyer.InsufficientPermissionsException;
import com.dbp.legalcheck.exception.lawyer.LawyerAlreadyExistsException;
import com.dbp.legalcheck.exception.lawyer.LawyerNotFoundException;
import com.dbp.legalcheck.exception.lawyer.UserNotLawyerException;
import com.dbp.legalcheck.infrastructure.lawyer.LawyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LawyerServiceTest {

    private LawyerRepository lawyerRepository;
    private ModelMapper modelMapper;
    private LawyerService lawyerService;

    @BeforeEach
    void setUp() {
        lawyerRepository = mock(LawyerRepository.class);
        modelMapper = new ModelMapper();
        lawyerService = new LawyerService(lawyerRepository, modelMapper);
    }

    @Test
    void getLawyerFromUser_success() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Lawyer lawyer = new Lawyer();
        lawyer.setId(user.getId());

        when(lawyerRepository.findById(user.getId())).thenReturn(Optional.of(lawyer));

        Lawyer result = lawyerService.getLawyerFromUser(user);

        assertEquals(lawyer, result);
    }

    @Test
    void getLawyerFromUser_notFound_throwsUserNotLawyerException() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(lawyerRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotLawyerException.class, () -> lawyerService.getLawyerFromUser(user));
    }

    @Test
    void listLawyers_success() {
        Lawyer lawyer = new Lawyer();
        lawyer.setFirstName("Test");

        when(lawyerRepository.findAll()).thenReturn(List.of(lawyer));

        List<LawyerResponseDTO> result = lawyerService.listLawyers();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getFirstName());
    }

    @Test
    void getLawyerById_success() {
        UUID id = UUID.randomUUID();
        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);
        lawyer.setFirstName("María");

        when(lawyerRepository.findById(id)).thenReturn(Optional.of(lawyer));

        LawyerResponseDTO result = lawyerService.getLawyerById(id);

        assertEquals("María", result.getFirstName());
    }

    @Test
    void getLawyerById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(lawyerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LawyerNotFoundException.class, () -> lawyerService.getLawyerById(id));
    }

    @Test
    void registerLawyer_success() {
        LawyerRequestDTO dto = new LawyerRequestDTO();
        dto.setEmail("test@lawyer.com");

        Lawyer lawyer = new Lawyer();
        lawyer.setEmail(dto.getEmail());

        when(lawyerRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(lawyerRepository.save(any())).thenReturn(lawyer);

        LawyerResponseDTO result = lawyerService.registerLawyer(dto);

        assertEquals("test@lawyer.com", result.getEmail());
    }

    @Test
    void registerLawyer_alreadyExists_throwsException() {
        LawyerRequestDTO dto = new LawyerRequestDTO();
        dto.setEmail("duplicate@lawyer.com");

        when(lawyerRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(LawyerAlreadyExistsException.class, () -> lawyerService.registerLawyer(dto));
    }

    @Test
    void updateLawyer_success_adminUser() {
        UUID id = UUID.randomUUID();
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setRole(UserRole.ADMINISTRADOR);

        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);

        LawyerRequestDTO dto = new LawyerRequestDTO();
        dto.setFirstName("Ana");

        when(lawyerRepository.findById(id)).thenReturn(Optional.of(lawyer));
        when(lawyerRepository.save(any())).thenReturn(lawyer);

        LawyerResponseDTO result = lawyerService.updateLawyer(id, dto, admin);

        assertEquals("Ana", result.getFirstName());
    }

    @Test
    void updateLawyer_success_ownerUser() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setRole(UserRole.ABOGADO);

        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);

        LawyerRequestDTO dto = new LawyerRequestDTO();
        dto.setFirstName("Ana");

        when(lawyerRepository.findById(id)).thenReturn(Optional.of(lawyer));
        when(lawyerRepository.save(any())).thenReturn(lawyer);

        LawyerResponseDTO result = lawyerService.updateLawyer(id, dto, user);

        assertEquals("Ana", result.getFirstName());
    }

    @Test
    void updateLawyer_insufficientPermissions_throwsException() {
        UUID id = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setRole(UserRole.ABOGADO);

        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);

        when(lawyerRepository.findById(id)).thenReturn(Optional.of(lawyer));

        assertThrows(InsufficientPermissionsException.class,
                () -> lawyerService.updateLawyer(id, new LawyerRequestDTO(), otherUser));
    }

    @Test
    void deleteLawyer_success_adminUser() {
        UUID id = UUID.randomUUID();
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setRole(UserRole.ADMINISTRADOR);

        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);

        when(lawyerRepository.findById(id)).thenReturn(Optional.of(lawyer));

        assertDoesNotThrow(() -> lawyerService.deleteLawyer(id, admin));
        verify(lawyerRepository).delete(lawyer);
    }

    @Test
    void deleteLawyer_insufficientPermissions_throwsException() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.ABOGADO);

        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);

        when(lawyerRepository.findById(id)).thenReturn(Optional.of(lawyer));

        assertThrows(InsufficientPermissionsException.class,
                () -> lawyerService.deleteLawyer(id, user));
    }

    @Test
    void deleteLawyer_lawyerNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setRole(UserRole.ADMINISTRADOR);

        when(lawyerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LawyerNotFoundException.class,
                () -> lawyerService.deleteLawyer(id, user));
    }
}
