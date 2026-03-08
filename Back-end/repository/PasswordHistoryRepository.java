package br.com.AutoStock.repository;

import br.com.AutoStock.model.PasswordHistory;
import br.com.AutoStock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> findTop5ByUserOrderByCreatedAtDesc(User user);
    void deleteByUserId(Long userId);
}
