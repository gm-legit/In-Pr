package com.example.Perfect_Gift.UsersRepository;

import com.example.Perfect_Gift.UserModel.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserModel, Integer> {

    Optional<UserModel> findUserByLoginAndPassword(String login, String password);
}
