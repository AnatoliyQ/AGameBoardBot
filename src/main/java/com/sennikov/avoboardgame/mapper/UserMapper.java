package com.sennikov.avoboardgame.mapper;

import com.sennikov.avoboardgame.dto.UserDto;
import com.sennikov.avoboardgame.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(UserModel entity) {
        if (entity == null) {
            return null;
        }

        return UserDto.builder()
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .telegramUsername(entity.getTelegramUsername())
                .isEmployee(entity.getIsEmployee())
                .build();

    }

    public UserModel toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        UserModel entity = new UserModel();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setTelegramUsername(dto.getTelegramUsername());
        entity.setIsEmployee(dto.getIsEmployee());

        return entity;
    }
}
