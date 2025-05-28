package com.dbp.legalcheck.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.email.EmailApplicationService;
import com.dbp.legalcheck.dto.user.ListedUserDTO;
import com.dbp.legalcheck.dto.user.RegisterUserDTO;
import com.dbp.legalcheck.exception.user.EmailConflictException;
import com.dbp.legalcheck.exception.user.OnlyAdminException;
import com.dbp.legalcheck.infrastructure.user.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailApplicationService emailApplicationService;

    // GET /api/users/{id}
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByVerificationId(UUID verificationId) {
        return userRepository.findByVerificationId(verificationId);
    }

    public boolean userExistsByRole(UserRole role) {
        return userRepository.existsByRole(role);
    }

    public User verifyUser(User user) {
        user.setVerificationId(null);
        User savedUser = userRepository.save(user);
        emailApplicationService.sendWelcomeEmail(savedUser);

        return savedUser;
    }

    public User createUser(RegisterUserDTO regist) {
        if (userRepository.existsByEmail(regist.getEmail())) {
            throw new EmailConflictException();
        }

        User user = modelMapper.map(regist, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationId(UUID.randomUUID());

        User savedUser = userRepository.save(user);
        emailApplicationService.sendSignInEmail(savedUser);

        return savedUser;
    }

    public User updateUserRole(User user, UserRole role) {
        if (user.getRole() == UserRole.ADMINISTRADOR
                && !userRepository.existsByRoleAndIdNot(UserRole.ADMINISTRADOR, user.getId())) {
            throw new OnlyAdminException();
        }

        user.setRole(role);
        return userRepository.save(user);
    }

    // GET /api/users
    public List<ListedUserDTO> list() {
        return userRepository.findAll()
                .stream().map(ListedUserDTO::new)
                .toList();
    }

    // GET /api/users/me
    public ListedUserDTO getUserInfo(User user) {
        return new ListedUserDTO(user);
    }
}
