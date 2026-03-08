package br.com.AutoStock.service;

import br.com.AutoStock.event.RegistrationCompleteEventListener;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.IAuthenticationService;
import br.com.AutoStock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final RegistrationCompleteEventListener registration;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User handleFailedLogin(User user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() >= 5) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
            try {
                registration.sendAccountLockedEmail(user);
            } catch (Exception e) {
                log.error("❌ Erro ao enviar e-mail de conta bloqueada para {}: {}", user.getEmail(), e.getMessage(), e);
            }
        }
        return userRepository.save(user);
    }

    public Optional<User> findByEmailOrCnpj(String login) {
        return userRepository.findByEmailOrCnpj(login);
    }

    @Override
    public void handleSuccessfulLogin(User user) {
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        User updated = userRepository.save(user);
        log.info("✅ Tentativas resetadas para: {}", updated.getFailedAttempts());
    }
    
    @Override
    public User salvar(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void generateAndSendVerificationCode(User user) {
        verificationCodeService.generateAndSendCode(user.getEmail());
    }

}
