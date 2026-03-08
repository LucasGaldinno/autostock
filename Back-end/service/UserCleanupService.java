package br.com.AutoStock.service;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.AutoStock.model.User;
import br.com.AutoStock.model.VerificationToken;
import br.com.AutoStock.repository.PasswordHistoryRepository;
import br.com.AutoStock.repository.UserRepository;
import br.com.AutoStock.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;

    @Scheduled(fixedDelayString = "PT7M")
    @Transactional
    public void deleteUnverifiedUsersAfterTokenExpiration() {
        Date now = new Date();
        List<VerificationToken> expiredTokens = tokenRepository.findByExpirationTimeBeforeAndUserEnabledFalse(now);

        for (VerificationToken token : expiredTokens) {
            User user = token.getUser();
            // Deleta tokens primeiro
            tokenRepository.delete(token);
            // Deleta histórico de senhas do usuário
            passwordHistoryRepository.deleteByUserId(user.getId());
            userRepository.delete(user);
            System.out.println("🧹 Usuário removido por não confirmar o e-mail: " + user.getEmail());
        }
    }
}
