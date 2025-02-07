package com.sennikov.avoboardgame.service;

import com.sennikov.avoboardgame.dto.UserDto;
import com.sennikov.avoboardgame.mapper.UserMapper;
import com.sennikov.avoboardgame.model.UserModel;
import com.sennikov.avoboardgame.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto saveUser(UserDto userDto) {
        log.debug("Saving new user: {}", userDto);
        UserModel userModel = userMapper.toEntity(userDto);
        UserModel savedModel = userRepository.save(userModel);
        return userMapper.toDto(savedModel);
    }

    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findUserByTelegramUsername(username).map(userMapper::toDto);
    }

    public Map<String, UserDto> getUsersByUsernames(List<String> usernames) {
        return userRepository.findAllByTelegramUsernameIn(usernames)
            .stream()
            .collect(Collectors.toMap(
                UserModel::getTelegramUsername,
                    userMapper::toDto,
                (u1, u2) -> u1
            ));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
