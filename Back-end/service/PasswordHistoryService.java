package br.com.AutoStock.service;

import br.com.AutoStock.model.PasswordHistory;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordHistoryService {

    private final PasswordHistoryRepository repository;
    private final PasswordEncoder passwordEncoder;

    public boolean isPasswordReused(User user, String newPassword) {
        List<PasswordHistory> ultimasSenhas = repository.findTop5ByUserOrderByCreatedAtDesc(user);
        return ultimasSenhas.stream()
                .anyMatch(registro -> passwordEncoder.matches(newPassword, registro.getPasswordHash()));
    }

    public void savePassword(User user, String encodedPassword) {
        PasswordHistory historico = new PasswordHistory();
        historico.setUser(user);
        historico.setPasswordHash(encodedPassword);
        repository.save(historico);
    }
}
