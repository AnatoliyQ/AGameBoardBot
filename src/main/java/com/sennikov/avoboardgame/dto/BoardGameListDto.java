package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardGameListDto {
    private Long id;
    private String name;
    private String type;
    private String players;
    private String owner;
    private String imageUrl;
} 