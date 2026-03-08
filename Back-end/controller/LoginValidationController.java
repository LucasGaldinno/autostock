package br.com.AutoStock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.AutoStock.config.AdminProperties;
import br.com.AutoStock.repository.EmployeeRepository;
import br.com.AutoStock.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/validate")
@RequiredArgsConstructor
public class LoginValidationController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminProperties adminProperties;
    
    @GetMapping("/login")
    public boolean validateLogin(@RequestParam String login) {

        // 🔥 Admin do .env sempre existe!
        if (login.equalsIgnoreCase(adminProperties.getEmail())) {
            return true;
        }
        
        if (employeeRepository.findByEmail(login).isPresent()) {
            return true;
        }

        // ⇩ Fluxo normal de usuário
        if (login.contains("@")) {
            return userRepository.findByEmail(login).isPresent();
        } else {
            return userRepository.findByCnpj(login).isPresent();
        }
    }
}
