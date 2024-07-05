package com.example.batchapidemo.controller;

import com.example.batchapidemo.entity.User;
import com.example.batchapidemo.exception.ResourceNotFoundException;
import com.example.batchapidemo.model.BatchOperation;
import com.example.batchapidemo.model.BatchRequest;
import com.example.batchapidemo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping({"","/"})
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("Found " + users.size() + " users");
        return users;
    }

    @GetMapping({"/{id}","/{id}/"})
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({"","/"})
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

//    @PatchMapping({"/{id}","/{id}/"})
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
//        Optional<User> optionalUser = userRepository.findById(id);
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            applyUpdates(user, updates);
//            User updatedUser = userRepository.save(user);
//            return ResponseEntity.ok(updatedUser);
//        }
//        return ResponseEntity.notFound().build();
//    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody JsonPatch patch) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            User patchedUser = applyPatchToUser(patch, user);
            userRepository.save(patchedUser);
            return ResponseEntity.ok(patchedUser);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping({"/batch","/batch/"})
    public ResponseEntity<List<Object>> batchOperations(@RequestBody BatchRequest batchRequest) {
        List<Object> results = new ArrayList<>();
        for (BatchOperation operation : batchRequest.getRequests()) {
            if ("PATCH".equalsIgnoreCase(operation.getMethod())) {
                String[] urlParts = operation.getUrl().split("/");
                Long userId = Long.parseLong(urlParts[urlParts.length - 1]);
                try {
                    JsonPatch patch = objectMapper.convertValue(operation.getBody(), JsonPatch.class);
                    ResponseEntity<User> result = updateUser(userId, patch);
                    results.add(result.getBody());
                } catch (IllegalArgumentException e) {
                    // Handle the case when the body cannot be converted to JsonPatch
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        return ResponseEntity.ok(results);
    }

    private User applyPatchToUser(JsonPatch patch, User targetUser) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
        return objectMapper.treeToValue(patched, User.class);
    }
    private void applyUpdates(User user, Map<String, Object> updates) {
        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("age")) {
            user.setAge((Integer) updates.get("age"));
        }
        if (updates.containsKey("city")) {
            user.setCity((String) updates.get("city"));
        }
    }
}
