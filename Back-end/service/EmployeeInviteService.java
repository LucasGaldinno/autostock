package br.com.AutoStock.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.AutoStock.event.RegistrationCompleteEventListener;
import br.com.AutoStock.model.EmployeeInvite;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.EmployeeInviteRepository;
import br.com.AutoStock.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeInviteService {

    private final EmployeeInviteRepository inviteRepository;
    private final RegistrationCompleteEventListener emailEventListener;
    private final EmployeeRepository employeeRepository;

    // ============================================
    // Gera convite e envia email
    // ============================================
    public void generateInvite(User agency, String employeeEmail) {
    	
    	if (employeeRepository.existsByEmail(employeeEmail)) {
    	    throw new RuntimeException("Este e-mail já pertence a um funcionário cadastrado.");
    	}

        // Verifica se já há convite ativo
        inviteRepository.findByEmailAndUsedFalseAndExpiresAtAfter(
                employeeEmail,
                LocalDateTime.now()
        ).ifPresent(invite -> {
            throw new RuntimeException("Já existe um convite ativo para este e-mail.");
        });

        // Gera token UUID único
        String token = UUID.randomUUID().toString();

        EmployeeInvite invite = new EmployeeInvite();
        invite.setEmail(employeeEmail);
        invite.setAgency(agency);
        invite.setToken(token);
        invite.setExpiresAt(LocalDateTime.now().plusHours(24)); // 24h
        invite.setUsed(false);

        inviteRepository.save(invite);

        // Monta URL do cadastro
        String inviteUrl = "http://localhost:8585/funcionario/cadastro?token=" + token;

        // Envia email usando o template central da aplicação
        emailEventListener.sendEmployeeInviteEmail(employeeEmail, agency, inviteUrl);
    }

    // ============================================
    // Validação do token
    // ============================================
    public EmployeeInvite validateToken(String token) {
        EmployeeInvite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido."));

        if (invite.isUsed()) {
            throw new RuntimeException("Este token já foi usado.");
        }

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Este token expirou.");
        }

        return invite;
    }
    
    public String validateInviteToken(String token) {

        Optional<EmployeeInvite> optional = inviteRepository.findByToken(token);

        if (optional.isEmpty()) {
            return "invalid";
        }

        EmployeeInvite invite = optional.get();

        if (invite.isUsed()) {
            return "used";
        }

        if (invite.isExpired()) {
            return "expired";
        }

        return "valid";
    }

    // ============================================
    // Marcar token como usado
    // ============================================
    public void markAsUsed(EmployeeInvite invite) {
        invite.setUsed(true);
        inviteRepository.save(invite);
    }
}
