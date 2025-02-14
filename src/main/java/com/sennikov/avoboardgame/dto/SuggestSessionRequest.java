package com.sennikov.avoboardgame.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestSessionRequest {
    @NotNull(message = "gameId не может быть пустым")
    private Long gameId;

    @NotNull(message = "dateTime не может быть пустым")
    private String dateTime; // ISO формат

    @NotBlank(message = "location не может быть пустым")
    private String location;

    @NotBlank(message = "chatId не может быть пустым")
    private String chatId;

    private Integer timezoneOffset; // Смещение в минутах
} 