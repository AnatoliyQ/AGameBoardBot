package com.sennikov.avoboardgame.repository;

import com.sennikov.avoboardgame.model.GameSessionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionPlayerRepository extends JpaRepository<GameSessionPlayer, Long> {
}