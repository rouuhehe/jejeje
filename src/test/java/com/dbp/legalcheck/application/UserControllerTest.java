package com.dbp.legalcheck.application;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbp.legalcheck.application.user.UserController;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.domain.user.UserService;
import com.dbp.legalcheck.dto.user.ListedUserDTO;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }
        @Bean
        public com.dbp.legalcheck.auth.domain.JwtService jwtService() {
            return mock(com.dbp.legalcheck.auth.domain.JwtService.class);
        }

    }

    // GET /api/users
    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void testListUsersShouldReturnUserList() throws Exception {
        ListedUserDTO dto = new ListedUserDTO();
        dto.setEmail("admin@example.com");
        List<ListedUserDTO> userList = List.of(dto);

        when(userService.list()).thenReturn(userList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@example.com"));
    }

    // GET /api/users/me
    @Test
    @WithMockUser(username = "user@example.com", roles = "USUARIO")
    void testGetUserInfoShouldReturnCurrentUser() throws Exception {
        ListedUserDTO dto = new ListedUserDTO();
        dto.setEmail("user@example.com");

        when(userService.getUserInfo(any())).thenReturn(dto);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    // GET /api/users/{id}
    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserByIdShouldReturnUser() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("found@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("found@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserByIdShouldReturnNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }
}
