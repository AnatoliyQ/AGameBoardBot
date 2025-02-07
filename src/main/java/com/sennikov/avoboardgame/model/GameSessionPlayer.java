package com.sennikov.avoboardgame.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_session_players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GameSessionPlayerId.class)
public class GameSessionPlayer {
    @Id
    @Column(name = "player_username")
    private String playerUsername;
    
    @Id
    @Column(name = "session_id")
    private Long sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private GameSession session;
    
    @Column(name = "is_winner")
    private Boolean isWinner;
}