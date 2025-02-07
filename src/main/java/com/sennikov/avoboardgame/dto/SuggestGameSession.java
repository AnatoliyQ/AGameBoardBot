package com.sennikov.avoboardgame.dto;

import com.sennikov.avoboardgame.model.BoardGame;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestGameSession {
    private Long id;

    private BoardGame game;

    private LocalDateTime sessionDateTime;

    private String location;

    private String pollMessageId;

    private String chatId;

    private LocalDateTime createdAt;
}
