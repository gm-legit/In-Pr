package com.example.Perfect_Gift.ServiceLayer;

import com.example.Perfect_Gift.UserModel.UserModel;
import com.example.Perfect_Gift.UsersRepository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public UserModel registerUser(String login, String password, String email, String nip, String phone_number) {
        if (login == null || password == null || email == null || nip == null || phone_number == null) {
            return null;
        } else {
            UserModel userModel = new UserModel();

            userModel.setLogin(login);
            userModel.setPassword(password);
            userModel.setEmail(email);
            userModel.setNip(login);
            userModel.setPhone_number(phone_number);
            return usersRepository.save(userModel);
        }
    }

    public UserModel authenticateUser(String login, String password)
    {
        return usersRepository.findUserByLoginAndPassword(login,password).orElse(null);
    }

}
