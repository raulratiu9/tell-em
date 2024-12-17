package com.tellem.controllers;

import com.tellem.models.User;
import com.tellem.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private UserRepository userRepository;

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setName("Alex Doe");
        user2.setEmail("alex@example.com");
        user2.setPassword("password432");
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
    }

    @Test
    public void testCreateUser() throws Exception {
        // Arrange
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\", \"email\":\"john@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}
