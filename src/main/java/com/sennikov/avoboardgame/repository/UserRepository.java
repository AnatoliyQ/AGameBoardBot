package com.sennikov.avoboardgame.repository;

import com.sennikov.avoboardgame.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findUserByTelegramUsername(String userName);
    List<UserModel> findAllByTelegramUsernameIn(List<String> usernames);
}
