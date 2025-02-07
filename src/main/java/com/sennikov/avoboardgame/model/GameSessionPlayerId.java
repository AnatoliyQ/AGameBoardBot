package com.sennikov.avoboardgame.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionPlayerId implements Serializable {
    private String playerUsername;
    private Long sessionId;
}