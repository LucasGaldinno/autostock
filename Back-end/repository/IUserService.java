package br.com.AutoStock.repository;

import java.util.List;
import java.util.Optional;

import br.com.AutoStock.dto.RegistrationRequest;
import br.com.AutoStock.model.User;

public interface IUserService {

    List<User> getAllUsers();

    User registerUser(RegistrationRequest registrationRequest);

    Optional<User> findByEmail(String email);

    User handleFailedLogin(User user);

    void handleSuccessfulLogin(User user);
}
