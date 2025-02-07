package com.sennikov.avoboardgame.controller;

import com.sennikov.avoboardgame.dto.UserDto;
import com.sennikov.avoboardgame.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(
            @Valid @RequestBody UserDto request,
            @RequestHeader(value = "X-Telegram-User-Id", required = false) Long userId) {
        log.info("Adding new user: {} by user: {}", request, userId);
        try {
            UserDto savedUser = userService.saveUser(request);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            log.error("Error adding game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        log.info("Get user by username: {}", username);
        return userService.getUserByUsername(username).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/batch")
    public ResponseEntity<Map<String, UserDto>> getUsers(@RequestParam("usernames") List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyMap());
        }
        
        log.info("Get users by usernames: {}", usernames);
        try {
            Map<String, UserDto> users = userService.getUsersByUsernames(usernames);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error getting users by usernames: {}", usernames, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Get all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

}