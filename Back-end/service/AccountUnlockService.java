package br.com.AutoStock.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.AutoStock.event.RegistrationCompleteEventListener;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountUnlockService {

    private final UserRepository userRepository;
    private final RegistrationCompleteEventListener registration;

    @Scheduled(fixedRate = 60000)
    public void unlockExpiredAccounts() {
        List<User> lockedUsers = userRepository.findByAccountLockedTrue();

        for (User user : lockedUsers) {
            if (user.isLockTimeExpired()) {
                user.setAccountLocked(false);
                user.setLockTime(null);
                user.setFailedAttempts(0);
                userRepository.save(user);

                try {
                    registration.sendAccountUnlockedEmail(user);
                } catch (Exception e) {
                    System.err.println("❌ Erro ao enviar e-mail de conta desbloqueada: " + e.getMessage());
                }
            }
        }
    }
}
