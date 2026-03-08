package br.com.AutoStock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.AutoStock.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCnpj(String cnpj);

	List<User> findByEnabledFalseAndCreatedAtBefore(LocalDateTime cutoffTime);
	
	List<User> findByAccountLockedTrue();
	long countByAccountLockedTrue();
	boolean existsByCnpj(String cnpj);
	boolean existsByEmail(String email);
	
	// Novo método para permitir buscar pelos dois
    @Query("SELECT u FROM User u WHERE u.email = :login OR u.cnpj = :login")
    Optional<User> findByEmailOrCnpj(@Param("login") String login);
}