package br.com.AutoStock.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.AutoStock.model.EmployeeInvite;
import br.com.AutoStock.model.User;

public interface EmployeeInviteRepository extends JpaRepository<EmployeeInvite, Long> {

    Optional<EmployeeInvite> findByToken(String token);

    boolean existsByToken(String token);
    
    void deleteByAgency(User agency);

    // Novo — busca convite ativo por email
    Optional<EmployeeInvite> findByEmailAndUsedFalseAndExpiresAtAfter(
            String email,
            LocalDateTime now
    );
}
