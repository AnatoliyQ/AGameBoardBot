package com.sennikov.avoboardgame.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "game_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_sessions_seq")
    @SequenceGenerator(name = "game_sessions_seq", sequenceName = "game_sessions_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "play_date")
    private LocalDate playDate;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSessionPlayer> players;

}
