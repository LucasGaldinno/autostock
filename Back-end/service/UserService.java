package br.com.AutoStock.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.AutoStock.repository.EmployeeInviteRepository;
import br.com.AutoStock.repository.EmployeeRepository;
import br.com.AutoStock.repository.IUserService;
import br.com.AutoStock.repository.PasswordHistoryRepository;
import br.com.AutoStock.repository.PasswordResetTokenRepository;
import br.com.AutoStock.dto.CnpjResponse;
import br.com.AutoStock.dto.RegistrationRequest;
import br.com.AutoStock.exception.CannotDeleteUserException;
import br.com.AutoStock.exception.CnpjDuplicadoException;
import br.com.AutoStock.exception.CnpjInvalidoException;
import br.com.AutoStock.exception.EmailDuplicadoException;
import br.com.AutoStock.model.Role;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.RoleRepository;
import br.com.AutoStock.repository.SaleContractRepository;
import br.com.AutoStock.repository.UserRepository;
import br.com.AutoStock.repository.VehicleImageRepository;
import br.com.AutoStock.repository.VehicleRepository;
import br.com.AutoStock.repository.VerificationTokenRepository;
import br.com.AutoStock.repository.WarrantyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CnpjApiService cnpjApiService;
    private final VehicleRepository vehicleRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmployeeRepository employeeRepository;
    private final WarrantyRepository warrantyRepository;
    private final EmployeeInviteRepository inviteRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final SaleContractRepository saleContractRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest r) {
        Role roleUser = roleRepository.findByNome("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Papel ROLE_USER não encontrado"));

     // --- Verificação de duplicidade ---
        if (userRepository.existsByEmail(r.getEmail())) {
            throw new EmailDuplicadoException(
                "Este e-mail já está em uso. Verifique sua caixa de entrada para confirmar o cadastro."
            );
        }

        if (userRepository.existsByCnpj(r.getCnpj())) {
            throw new CnpjDuplicadoException(
               "Este CNPJ já está em uso. Verifique sua caixa de entrada para confirmar o cadastro."
            );
        }

        // Consulta e valida situação do CNPJ
        CnpjResponse cnpj = cnpjApiService.consultarCnpj(r.getCnpj());
        if (cnpj == null || cnpj.getSituacao() == null
            || !cnpj.getSituacao().trim().equalsIgnoreCase("ATIVA")) {
            throw new CnpjInvalidoException("CNPJ inválido ou inativo");
        }

        // Criação do usuário com construtor completo
        User u = new User(
            r.getEmail(),
            passwordEncoder.encode(r.getPassword()),
            r.getCnpj(),
            r.getRazaoSocial(),
            r.getNomeFantasia(),
            r.getInscricaoEstadual(),
            r.getCep(),
            r.getLogradouro(),
            r.getNumero(),
            r.getComplemento(),
            r.getBairro(),
            r.getCidade(),
            r.getUf(),
            r.getTelefone(),
            List.of(roleUser)
        );

        log.info(">>> Salvando usuário {} com CNPJ {}", r.getEmail(), r.getCnpj());

        return userRepository.save(u);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByCnpj(String cnpj) {
        return userRepository.findByCnpj(cnpj);
    }

    public Optional<User> getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String login = userDetails.getUsername();

            // 1) Tenta achar uma AGÊNCIA (User) por e-mail ou CNPJ
            Optional<User> agencyOpt = userRepository.findByEmailOrCnpj(login);
            if (agencyOpt.isPresent()) {
                return agencyOpt;
            }

            // 2) Se não achou, tenta achar um FUNCIONÁRIO por e-mail
            return employeeRepository.findByEmail(login)
                    .map(emp -> emp.getAgency()); // retorna a agência DONA do funcionário
        }

        return Optional.empty();
    }

    
    @Override
    public User handleFailedLogin(User user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);

        if (user.getFailedAttempts() >= 5) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
        }

        return userRepository.save(user);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void handleSuccessfulLogin(User user) {
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }
    
    public User getLoggedUser() {
        return getUsuarioLogado()
            .orElseThrow(() -> new IllegalStateException("Usuário não autenticado."));
    }
    
    @Transactional
    public void deleteUser(User user) {

        try {
            Long agencyId = user.getId();
            Long userId = user.getId();
            
            passwordResetTokenRepository.deleteByUserId(userId);

            warrantyRepository.deleteByUser(user);

            saleContractRepository.deleteByUser(user);

            employeeRepository.deleteByAgencyId(agencyId);

            inviteRepository.deleteByAgency(user);

            vehicleImageRepository.deleteByVehicle_User(user);

            vehicleRepository.deleteByUser(user);

            passwordHistoryRepository.deleteByUserId(agencyId);
            verificationTokenRepository.deleteByUser(user);

            user.getRoles().clear();
            userRepository.save(user);

            userRepository.delete(user);

        } catch (Exception e) {
            throw new CannotDeleteUserException(
                    "Não foi possível excluir a conta. Existem vínculos pendentes. Erro: " + e.getMessage()
            );
        }
    }
    
    public boolean existsEmployeeByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

}
