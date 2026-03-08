package br.com.AutoStock.controller;

import br.com.AutoStock.model.Employee;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.EmployeeRepository;
import br.com.AutoStock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeManagementController {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String listEmployees(
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "deleted", required = false) String deleted,
            Model model
    ) {

        // SEMPRE FUNCIONA
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName(); // email do usuário logado (agência)
        User agency = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Agência não encontrada"));

        model.addAttribute("employees", employeeRepository.findByAgency(agency));

        if (success != null) {
            model.addAttribute("toastSuccess", "Funcionário atualizado com sucesso!");
        }

        if (deleted != null) {
            model.addAttribute("toastSuccess", "Funcionário deletado com sucesso!");
        }

        return "employee/employee-list";
    }


    @GetMapping("/edit/{id}")
    public String showEditPage(
            @PathVariable Long id,
            Model model
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User agency = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Employee employee = employeeRepository
                .findByIdAndAgency_Id(id, agency.getId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

        model.addAttribute("employee", employee);

        return "employee/employee-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @ModelAttribute("employee") Employee updated
    ) {

        // Pega usuário logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User agency = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Garante que pertence à agência logada
        Employee employee = employeeRepository
                .findByIdAndAgency_Id(id, agency.getId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

        // Atualiza dados
        employee.setFirstName(updated.getFirstName());
        employee.setLastName(updated.getLastName());
        employee.setPhone(updated.getPhone());
        employee.setCep(updated.getCep());
        employee.setLogradouro(updated.getLogradouro());
        employee.setNumero(updated.getNumero());
        employee.setComplemento(updated.getComplemento());
        employee.setBairro(updated.getBairro());
        employee.setCidade(updated.getCidade());
        employee.setUf(updated.getUf());

        employeeRepository.save(employee);

        return "redirect:/employees?success=1"; // e aqui era employees e não employee
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User agency = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Employee employee = employeeRepository
                .findByIdAndAgency_Id(id, agency.getId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

        employeeRepository.delete(employee);

        return "redirect:/employees?deleted=1";
    }
}
