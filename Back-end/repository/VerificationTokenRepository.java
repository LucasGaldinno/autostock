package br.com.AutoStock.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.AutoStock.model.User;
import br.com.AutoStock.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
	Optional<VerificationToken> findByToken(String token);
	
	List<VerificationToken> findByExpirationTimeBeforeAndUserEnabledFalse(Date now);
	
	void deleteByUser(User user);
}
