package com.dbp.legalcheck.auth.application;

import com.dbp.legalcheck.auth.domain.AuthenticationService;
import com.dbp.legalcheck.auth.dto.AuthLoginDTO;
import com.dbp.legalcheck.auth.dto.AuthResponseDTO;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.domain.user.UserService;
import com.dbp.legalcheck.dto.user.RegisterUserDTO;
import com.dbp.legalcheck.dto.user.UserResponseDTO;
import com.dbp.legalcheck.dto.user.VerifyResendDTO;
import com.dbp.legalcheck.dto.user.VerifyUserDTO;
import com.dbp.legalcheck.event.SignInEmailEvent;
import com.dbp.legalcheck.exception.user.UserAlreadyVerifiedException;
import com.dbp.legalcheck.exception.user.UserNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthLoginDTO request) {
        return authenticationService.jwtLogin(request);
    }

    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody RegisterUserDTO request) {
        User createdUser = userService.createUser(request);
        return modelMapper.map(createdUser, UserResponseDTO.class);
    }

    @PostMapping("/verify")
    public UserResponseDTO verifyUser(@Valid @RequestBody VerifyUserDTO request) {
        User user = authenticationService.verifyUser(request.getVerificationId());
        UserResponseDTO response = modelMapper.map(user, UserResponseDTO.class);
        response.setVerified(true);
        return response;
    }

    @PostMapping("/verify-resend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendVerificationEmail(@Valid @RequestBody VerifyResendDTO request) {
        User user = userService.getUserByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (user.verified()) {
            throw new UserAlreadyVerifiedException();
        }

        eventPublisher.publishEvent(new SignInEmailEvent(user));
    }

}
