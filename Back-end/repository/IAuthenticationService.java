package br.com.AutoStock.repository;

import br.com.AutoStock.model.User;

import java.util.Optional;

public interface IAuthenticationService {
    Optional<User> findByEmail(String email);
    User handleFailedLogin(User user);
    void handleSuccessfulLogin(User user);
	void generateAndSendVerificationCode(User user);
	User salvar(User user);
    Optional<User> findByEmailOrCnpj(String login);
}
