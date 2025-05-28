package com.dbp.legalcheck.application.user;

import java.util.List;
import java.util.UUID;

import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.domain.user.UserService;
import com.dbp.legalcheck.dto.user.ListedUserDTO;
import com.dbp.legalcheck.exception.user.UserNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<ListedUserDTO> listUsers() {
        return userService.list();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ListedUserDTO getUserInfo(@AuthenticationPrincipal User user) {
        return userService.getUserInfo(user);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
