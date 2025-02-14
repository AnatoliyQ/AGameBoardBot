package com.sennikov.avoboardgame.repository;

import com.sennikov.avoboardgame.model.BoardGame;
import com.sennikov.avoboardgame.dto.BoardGameListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {
    List<BoardGame> findAllByOrderByIdDesc();

    @Query("SELECT COUNT(DISTINCT bg.owner) FROM BoardGame bg")
    long countDistinctOwner();

    @Query("SELECT new com.sennikov.avoboardgame.dto.BoardGameListDto(" +
           "bg.id, bg.name, bg.type, bg.players, bg.owner, bg.imageUrl) " +
           "FROM BoardGame bg ORDER BY bg.id DESC")
    List<BoardGameListDto> findAllForList();

    @Query("SELECT bg FROM BoardGame bg WHERE bg.id = :id")
    Optional<BoardGame> findGameDetails(@Param("id") Long id);
}