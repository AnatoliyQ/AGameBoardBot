package com.sennikov.avoboardgame.mapper;

import com.sennikov.avoboardgame.dto.BoardGameDto;
import com.sennikov.avoboardgame.dto.BoardGameRequest;
import com.sennikov.avoboardgame.model.BoardGame;
import org.springframework.stereotype.Component;

@Component
public class BoardGameMapper {

    public BoardGameDto toDto(BoardGame entity) {
        if (entity == null) {
            return null;
        }

        return BoardGameDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .players(entity.getPlayers())
                .owner(entity.getOwner())
                .imageUrl(entity.getImageUrl())
                .description(entity.getDescription())
                .build();
    }

    public BoardGame toEntity(BoardGameRequest request) {
        if (request == null) {
            return null;
        }

        BoardGame game = new BoardGame();
        game.setName(request.getName());
        game.setType(request.getType());
        game.setPlayers(request.getPlayers());
        game.setOwner(request.getOwner());
        game.setImageUrl(request.getImageUrl());
        game.setDescription(request.getDescription());
        return game;
    }
}