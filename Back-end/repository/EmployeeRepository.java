package br.com.AutoStock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.AutoStock.model.Employee;
import br.com.AutoStock.model.User;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Buscar funcionário pelo email
    Optional<Employee> findByEmail(String email);

    // Buscar funcionário pelo CPF
    Optional<Employee> findByCpf(String cpf);

    // Listar funcionários de uma agência específica
    List<Employee> findByAgency(User agency);
    
    List<Employee> findByAgency_Id(Long agencyId);

    Optional<Employee> findByIdAndAgency_Id(Long id, Long agencyId);
    
    void deleteByAgencyId(Long agencyId);

    // Verificar duplicidade
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByRg(String rg);
}
