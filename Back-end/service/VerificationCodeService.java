package br.com.AutoStock.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import br.com.AutoStock.event.RegistrationCompleteEventListener;
import br.com.AutoStock.model.VerificationCode;
import br.com.AutoStock.repository.VerificationCodeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository codeRepo;
    private final RegistrationCompleteEventListener registration;

    @Transactional
    public void generateAndSendCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        codeRepo.deleteByEmail(email);
        codeRepo.save(new VerificationCode(email, code, expiry));

        registration.sendVerificationCodeEmail(email, code);
    }



    public boolean validateCode(String email, String inputCode) {
        return codeRepo.findByEmail(email)
                .filter(code -> !code.isExpired() && code.getCode().equals(inputCode))
                .map(code -> {
                    codeRepo.delete(code);
                    return true;
                }).orElse(false);
    }
}

