package br.com.AutoStock.repository;

import java.util.Optional;

import br.com.AutoStock.model.User;
import br.com.AutoStock.model.VerificationToken;

public interface IVerificationTokenService {
    String validateToken(String token);
    void saveVerificationTokenForUser(User user, String token);
    Optional<VerificationToken> findByToken(String token);
}
