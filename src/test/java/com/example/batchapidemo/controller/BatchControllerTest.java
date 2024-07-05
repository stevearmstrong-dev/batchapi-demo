package com.example.batchapidemo.controller;

import com.example.batchapidemo.entity.User;
import com.example.batchapidemo.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BatchController.class)
public class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testBatchUserOperations() throws Exception {
        // Arrange
        User user1 = new User(1L, "John", "john@example.com", 25, "New York");
        User user2 = new User(2L, "Jane", "jane@example.com", 30, "London");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("operations", Arrays.asList(
                Map.of("method", "PATCH", "url", "/api/users/1", "body", Map.of("name", "John Updated", "age", 26)),
                Map.of("method", "PATCH", "url", "/api/users/2", "body", Map.of("city", "Paris"))
        ));

        // Act
        MvcResult result = mockMvc.perform(post("/api/batch/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String content = result.getResponse().getContentAsString();
        List<User> responseUsers = objectMapper.readValue(content, new TypeReference<List<User>>() {});

        // Detailed assertions
        assertThat(responseUsers).hasSize(2);

        User updatedUser1 = responseUsers.get(0);
        assertThat(updatedUser1.getId()).isEqualTo(1L);
        assertThat(updatedUser1.getName()).isEqualTo("John Updated");
        assertThat(updatedUser1.getAge()).isEqualTo(26);
        assertThat(updatedUser1.getEmail()).isEqualTo("john@example.com");
        assertThat(updatedUser1.getCity()).isEqualTo("New York");

        User updatedUser2 = responseUsers.get(1);
        assertThat(updatedUser2.getId()).isEqualTo(2L);
        assertThat(updatedUser2.getName()).isEqualTo("Jane");
        assertThat(updatedUser2.getAge()).isEqualTo(30);
        assertThat(updatedUser2.getEmail()).isEqualTo("jane@example.com");
        assertThat(updatedUser2.getCity()).isEqualTo("Paris");

        // Log the results
        System.out.println("Test Results:");
        System.out.println("User 1 (Expected):");
        System.out.println("  ID: 1, Name: John Updated, Email: john@example.com, Age: 26, City: New York");
        System.out.println("User 1 (Actual):");
        System.out.println("  " + updatedUser1);

        System.out.println("User 2 (Expected):");
        System.out.println("  ID: 2, Name: Jane, Email: jane@example.com, Age: 30, City: Paris");
        System.out.println("User 2 (Actual):");
        System.out.println("  " + updatedUser2);
    }
}
