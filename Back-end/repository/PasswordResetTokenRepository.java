package br.com.AutoStock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.AutoStock.model.PasswordResetToken;
import br.com.AutoStock.model.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	
    Optional<PasswordResetToken> findByToken(String theToken);

	Optional<PasswordResetToken> findByUser(User user);
	
	void deleteByUserId(Long userId);
}