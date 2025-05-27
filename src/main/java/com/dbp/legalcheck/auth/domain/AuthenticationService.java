package com.dbp.legalcheck.auth.domain;

import java.util.UUID;

import com.dbp.legalcheck.auth.dto.AuthLoginDTO;
import com.dbp.legalcheck.auth.dto.AuthResponseDTO;
import com.dbp.legalcheck.auth.exception.InvalidCredentialsException;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.domain.user.UserService;
import com.dbp.legalcheck.exception.user.UserNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO jwtLogin(AuthLoginDTO login) {
        User user = userService.getUserByEmail(login.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(jwtService.generateToken(user));
        return response;
    }

    public User verifyUser(UUID verificationId) {
        User user = userService.getUserByVerificationId(verificationId)
                .orElseThrow(UserNotFoundException::new);
        return userService.verifyUser(user);
    }
}
