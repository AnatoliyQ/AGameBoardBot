package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardGameDto {
    private Long id;
    private String name;
    private String type;
    private String players;
    private String owner;
    private String imageUrl;
    private String description;
}
