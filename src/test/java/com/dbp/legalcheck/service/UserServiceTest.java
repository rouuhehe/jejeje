package com.dbp.legalcheck.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.email.EmailApplicationService;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.domain.user.UserService;
import com.dbp.legalcheck.dto.user.RegisterUserDTO;
import com.dbp.legalcheck.dto.user.ListedUserDTO;
import com.dbp.legalcheck.exception.user.EmailConflictException;
import com.dbp.legalcheck.exception.user.OnlyAdminException;
import com.dbp.legalcheck.infrastructure.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private EmailApplicationService emailService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        modelMapper = new ModelMapper();
        passwordEncoder = mock(PasswordEncoder.class);
        emailService = mock(EmailApplicationService.class);

        userService = new UserService(userRepository, modelMapper, passwordEncoder, emailService);
    }

    @Test
    void shouldReturnUserById() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("123456");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");

        User userEntity = modelMapper.map(dto, User.class);
        userEntity.setPassword("hashedPassword");
        userEntity.setVerificationId(UUID.randomUUID());

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User createdUser = userService.createUser(dto);

        assertNotNull(createdUser.getVerificationId());
        assertEquals("hashedPassword", createdUser.getPassword());
        verify(emailService).sendSignInEmail(any(User.class));
    }

    @Test
    void shouldThrowEmailConflictException() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("test@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(EmailConflictException.class, () -> userService.createUser(dto));
    }

    @Test
    void shouldThrowOnlyAdminExceptionIfLastAdmin() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.ADMINISTRADOR);

        when(userRepository.existsByRoleAndIdNot(UserRole.ADMINISTRADOR, user.getId())).thenReturn(false);

        assertThrows(OnlyAdminException.class, () -> userService.updateUserRole(user, UserRole.USUARIO));
    }

    @Test
    void shouldUpdateUserRole() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.ADMINISTRADOR);

        when(userRepository.existsByRoleAndIdNot(UserRole.ADMINISTRADOR, user.getId())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updated = userService.updateUserRole(user, UserRole.USUARIO);

        assertEquals(UserRole.USUARIO, updated.getRole());
    }

    @Test
    void shouldVerifyUser() {
        User user = new User();
        user.setVerificationId(UUID.randomUUID());

        when(userRepository.save(user)).thenReturn(user);

        User verifiedUser = userService.verifyUser(user);

        assertNull(verifiedUser.getVerificationId());
    }

    @Test
    void shouldListUsers() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<ListedUserDTO> result = userService.list();

        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void shouldReturnUserInfo() {
        User user = new User();
        user.setEmail("me@example.com");

        ListedUserDTO dto = userService.getUserInfo(user);

        assertEquals("me@example.com", dto.getEmail());
    }
}
