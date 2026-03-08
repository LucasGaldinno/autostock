package br.com.AutoStock.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.AutoStock.repository.IPasswordResetTokenService;
import br.com.AutoStock.model.PasswordResetToken;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.PasswordResetTokenRepository;
import br.com.AutoStock.repository.UserRepository;
import br.com.AutoStock.utility.TokenExpirationTime;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService implements IPasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;

    @Override
    public String validatePasswordResetToken(String theToken) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(theToken);

        if (tokenOptional.isEmpty()) {
            System.out.println("Token inválido!");
            return "invalid";
        }

        PasswordResetToken token = tokenOptional.get();

        LocalDateTime expiration = token.getExpirationTime().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime();

        if (LocalDateTime.now().isAfter(expiration)) {
            System.out.println("Token expirado!");
            return "expired";
        }

        return "valid";
    }
    
    @Override
    public Optional<User> findUserByPasswordResetToken(String theToken) {
    	return passwordResetTokenRepository.findByToken(theToken)
                .map(PasswordResetToken::getUser)
                .filter(user -> user != null);
    }

    @Override
    public void resetPassword(User theUser, String encodedPassword) {
        theUser.setPassword(encodedPassword);
        userRepository.save(theUser);
    }
    
    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
    	Optional<PasswordResetToken> existingToken = passwordResetTokenRepository.findByUser(user);

        PasswordResetToken resetToken;
        if (existingToken.isPresent()) {
            // Atualiza o token e a data de expiração
            resetToken = existingToken.get();
            resetToken.setToken(token);
            resetToken.setExpirationTime(TokenExpirationTime.getPasswordResetExpirationTime());
        } else {
            // Cria novo token se não existir
            resetToken = new PasswordResetToken(token, user);
        }

        passwordResetTokenRepository.save(resetToken);
    }
}