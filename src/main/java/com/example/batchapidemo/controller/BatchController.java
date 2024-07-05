package com.example.batchapidemo.controller;

import com.example.batchapidemo.entity.User;
import com.example.batchapidemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<List<Object>> batchUserOperations(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> operations = (List<Map<String, Object>>) request.get("operations");
        List<Object> results = new ArrayList<>();

        for (Map<String, Object> operation : operations) {
            String method = (String) operation.get("method");
            String url = (String) operation.get("url");
            Map<String, Object> body = (Map<String, Object>) operation.get("body");

            if ("PATCH".equalsIgnoreCase(method)) {
                String[] urlParts = url.split("/");
                Long userId = Long.parseLong(urlParts[urlParts.length - 1]);
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    updateUserFields(user, body);
                    results.add(userRepository.save(user));
                } else {
                    results.add("User not found: " + userId);
                }
            }
            // Add handling for other methods (POST, GET, etc.) here
        }

        return ResponseEntity.ok(results);
    }

    private void updateUserFields(User user, Map<String, Object> updates) {
        if (updates.containsKey("name")) user.setName((String) updates.get("name"));
        if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
        if (updates.containsKey("age")) user.setAge(((Number) updates.get("age")).intValue());
        if (updates.containsKey("city")) user.setCity((String) updates.get("city"));
    }
}
