package com.dbp.legalcheck.application;

import com.dbp.legalcheck.PostgresTestContainersConfiguration;
import com.dbp.legalcheck.common.enums.UserRole;
import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.infrastructure.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(PostgresTestContainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User user;

    // Test de listUsers
    @Test
    public void shouldReturnUsers_WhenAdminListUsers() throws Exception {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("Pancho");
        user.setLastName("Villa");
        user.setEmail("pvilla@gmail.com");
        user.setPassword("PVNRT");
        user.setPhoneNumber("987654321");
        user.setRole(UserRole.ADMINISTRADOR);
        userRepository.save(user);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("Tulio");
        user.setLastName("Triviño");
        user.setEmail("ttriviño@gmail.com");
        user.setPassword("TulioTriviño");
        user.setPhoneNumber("999888777");
        user.setRole(UserRole.USUARIO);
        userRepository.save(user);

        mockMvc.perform(get("/api/users").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // Test de getUserInfo
    @Test
    public void shoulReturnUserInfo_WhenRequestedByUser() throws Exception {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("Miku");
        user.setLastName("Hatsune");
        user.setEmail("01miku@gmail.com");
        user.setPassword("Mikuuu");
        user.setPhoneNumber("987654333");
        user.setRole(UserRole.USUARIO);
        userRepository.save(user);

        mockMvc.perform(get("/api/users/me").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Miku"))
                .andExpect(jsonPath("$.lastName").value("Hatsune"));

    }

    // Test de getUserById
    @Test
    public void shouldReturnUserById_WhenRequestedByAdmin() throws Exception {
        User pancho = new User();
        pancho.setId(UUID.randomUUID());
        pancho.setFirstName("Pancho");
        pancho.setLastName("Villa");
        pancho.setEmail("pvilla@gmail.com");
        pancho.setPassword("PVNRT");
        pancho.setPhoneNumber("987654321");
        pancho.setRole(UserRole.ADMINISTRADOR);
        userRepository.save(pancho);

        User tulio = new User();
        tulio.setId(UUID.randomUUID());
        tulio.setFirstName("Tulio");
        tulio.setLastName("Triviño");
        tulio.setEmail("ttriviño@gmail.com");
        tulio.setPassword("TulioTriviño");
        tulio.setPhoneNumber("999888777");
        tulio.setRole(UserRole.USUARIO);
        userRepository.save(tulio);

        mockMvc.perform(get("/api/users/" + tulio.getId()).with(user(pancho)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Tulio"))
                .andExpect(jsonPath("$.lastName").value("Triviño"));
    }
}
