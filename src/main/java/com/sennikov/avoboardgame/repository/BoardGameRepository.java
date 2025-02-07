package com.sennikov.avoboardgame.repository;

import com.sennikov.avoboardgame.model.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {
    List<BoardGame> findAllByOrderByIdDesc();

    @Query("SELECT COUNT(DISTINCT bg.owner) FROM BoardGame bg")
    long countDistinctOwner();
}