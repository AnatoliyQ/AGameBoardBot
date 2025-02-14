package com.sennikov.avoboardgame.service;

import com.sennikov.avoboardgame.dto.BoardGameDto;
import com.sennikov.avoboardgame.dto.BoardGameRequest;
import com.sennikov.avoboardgame.dto.BoardGameListDto;
import com.sennikov.avoboardgame.mapper.BoardGameMapper;
import com.sennikov.avoboardgame.model.BoardGame;
import com.sennikov.avoboardgame.repository.BoardGameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class BoardGameService {
    private final BoardGameRepository gameRepository;
    private final BoardGameMapper gameMapper;

    @Transactional(readOnly = true)
    public List<BoardGameListDto> getAllGames() {
        log.debug("Getting all games (list view)");
        return gameRepository.findAllForList();
    }

    public BoardGameDto saveGame(BoardGameRequest request) {
        log.debug("Saving new game: {}", request);
        BoardGame game = gameMapper.toEntity(request);
        BoardGame savedGame = gameRepository.save(game);
        return gameMapper.toDto(savedGame);
    }

    @Transactional(readOnly = true)
    public Optional<BoardGameDto> getGameById(Long id) {
        log.debug("Getting game details by id: {}", id);
        return gameRepository.findGameDetails(id)
                .map(gameMapper::toDto);
    }

    public BoardGameDto updateGame(Long id, BoardGameRequest request) {
        log.debug("Updating game with id: {}", id);
        return gameRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setName(request.getName());
                    existingGame.setType(request.getType());
                    existingGame.setPlayers(request.getPlayers());
                    existingGame.setOwner(request.getOwner());
                    BoardGame updatedGame = gameRepository.save(existingGame);
                    return gameMapper.toDto(updatedGame);
                })
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + id));
    }

    public void deleteGame(Long id) {
        log.debug("Deleting game with id: {}", id);
        if (!gameRepository.existsById(id)) {
            throw new EntityNotFoundException("Game not found with id: " + id);
        }
        gameRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return gameRepository.existsById(id);
    }

}
