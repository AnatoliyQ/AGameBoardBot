package com.sennikov.avoboardgame.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuggestSessionRequest {
    @NotNull(message = "gameId не может быть пустым")
    private Long gameId;

    @NotNull(message = "dateTime не может быть пустым")
    private LocalDateTime dateTime;

    @NotBlank(message = "location не может быть пустым")
    private String location;

    @NotBlank(message = "chatId не может быть пустым")
    private String chatId;
} 