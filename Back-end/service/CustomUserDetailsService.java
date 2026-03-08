package br.com.AutoStock.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.AutoStock.config.AdminProperties;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Employee;
import br.com.AutoStock.repository.UserRepository;
import br.com.AutoStock.repository.EmployeeRepository;
import br.com.AutoStock.security.CustomUserDetails;
import br.com.AutoStock.security.EmployeeUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminProperties adminProperties;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        // 1) ADMIN carregado do .env
        if (login.equalsIgnoreCase(adminProperties.getEmail())) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(adminProperties.getEmail())
                    .password(passwordEncoder.encode(adminProperties.getPassword()))
                    .roles("ADMIN")
                    .build();
        }

        // 2) Login via E-MAIL (pode ser agência OU funcionário)
        if (isValidEmail(login)) {

            // Tenta achar uma AGÊNCIA por e-mail
            User user = userRepository.findByEmail(login).orElse(null);
            if (user != null) {
                return new CustomUserDetails(user);
            }

            // Não achou agência? Tenta FUNCIONÁRIO por e-mail
            Employee employee = employeeRepository.findByEmail(login).orElse(null);
            if (employee != null) {
                return new EmployeeUserDetails(employee);
            }

            throw new UsernameNotFoundException("Nenhum usuário ou funcionário encontrado com o e-mail: " + login);
        }

        // 3) Login via CNPJ (somente agência)
        if (isValidCnpj(login)) {
            User user = userRepository.findByCnpj(login)
                    .orElseThrow(() -> new UsernameNotFoundException("Agência não encontrada com o CNPJ: " + login));

            return new CustomUserDetails(user);
        }

        throw new UsernameNotFoundException("Formato de login inválido. Use e-mail ou CNPJ.");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidCnpj(String cnpj) {
        return cnpj != null && cnpj.matches("\\d{14}");
    }
}
