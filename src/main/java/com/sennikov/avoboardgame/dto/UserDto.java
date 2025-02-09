package com.sennikov.avoboardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String firstName;

    private String lastName;

    private String telegramUsername;

    private Boolean isEmployee;
}
