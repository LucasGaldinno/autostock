package br.com.AutoStock.config;

import org.springframework.stereotype.Component;
import br.com.AutoStock.model.Role;
import br.com.AutoStock.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleSeeder {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void seedRoles() {
        if (roleRepository.findByNome("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role("ROLE_USER"));
        }
        if (roleRepository.findByNome("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
        if (roleRepository.findByNome("ROLE_EMPLOYEE").isEmpty()) {
            roleRepository.save(new Role("ROLE_EMPLOYEE"));
        }
    }
}
