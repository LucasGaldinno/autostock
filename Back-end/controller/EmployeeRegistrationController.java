package br.com.AutoStock.controller;

import br.com.AutoStock.dto.EmployeeRegistrationDTO;
import br.com.AutoStock.model.Employee;
import br.com.AutoStock.model.EmployeeInvite;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.EmployeeRepository;
import br.com.AutoStock.service.EmployeeInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.beans.PropertyEditorSupport;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/funcionario")
@RequiredArgsConstructor
public class EmployeeRegistrationController {

    private final EmployeeInviteService inviteService;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/cadastro")
    public String showRegistrationPage(@RequestParam("token") String token, Model model) {

        String status = inviteService.validateInviteToken(token);

        switch (status) {
            case "invalid":
                return "redirect:/error?invite_invalid";
            case "expired":
                return "redirect:/error?invite_expired";
            case "used":
                return "redirect:/error?invite_used";
        }

        EmployeeInvite invite = inviteService.validateToken(token);

        EmployeeRegistrationDTO dto = new EmployeeRegistrationDTO();
        dto.setEmail(invite.getEmail());

        model.addAttribute("invite", invite);
        model.addAttribute("employeeDTO", dto);

        return "employee/employee-registration";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, "cpf", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text != null ? text.replaceAll("\\D", "") : null);
            }
        });

        binder.registerCustomEditor(String.class, "rg", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text != null ? text.replaceAll("\\D", "") : null);
            }
        });

        binder.registerCustomEditor(String.class, "cep", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text != null ? text.replaceAll("\\D", "") : null);
            }
        });

        binder.registerCustomEditor(String.class, "phone", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text != null ? text.replaceAll("\\D", "") : null);
            }
        });
    }

    @PostMapping("/cadastro")
    public String processRegistration(
            @RequestParam("token") String token,
            @Valid @ModelAttribute("employeeDTO") EmployeeRegistrationDTO dto,
            BindingResult result,
            Model model
    ) {
    	
    	String status = inviteService.validateInviteToken(token);

        if (!status.equals("valid")) {
            return "redirect:/error?invite_" + status;
        }
    	
        EmployeeInvite invite = inviteService.validateToken(token);
        User agency = invite.getAgency();

        // 🔥 Se houver erro de validação (senha fraca, CPF inválido etc.)
        if (result.hasErrors()) {

            // devolve invite e DTO preenchido
            model.addAttribute("invite", invite);
            model.addAttribute("employeeDTO", dto);

            // devolve errors ao Thymeleaf
            model.addAttribute("org.springframework.validation.BindingResult.employeeDTO", result);

            return "employee/employee-registration";
        }

        if (employeeRepository.existsByCpf(dto.getCpf())) {
            model.addAttribute("invite", invite);
            model.addAttribute("employeeDTO", dto);
            model.addAttribute("error", "Já existe um funcionário com este CPF.");
            return "employee/employee-registration";
        }

        if (employeeRepository.existsByEmail(invite.getEmail())) {
            model.addAttribute("invite", invite);
            model.addAttribute("employeeDTO", dto);
            model.addAttribute("error", "Este convite já foi utilizado.");
            return "employee/employee-registration";
        }
        
        if (employeeRepository.existsByRg(dto.getRg())) {
            model.addAttribute("invite", invite);
            model.addAttribute("employeeDTO", dto);
            model.addAttribute("error", "Já existe um funcionário com este RG.");
            return "employee/employee-registration";
        }


        // Cria funcionário
        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setCpf(dto.getCpf());
        employee.setRg(dto.getRg());
        employee.setEmail(invite.getEmail());
        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
        employee.setPhone(dto.getPhone());
        employee.setCep(dto.getCep());
        employee.setLogradouro(dto.getStreet());
        employee.setNumero(dto.getNumber());
        employee.setComplemento(dto.getComplement());
        employee.setBairro(dto.getDistrict());
        employee.setCidade(dto.getCity());
        employee.setUf(dto.getState());
        employee.setAgency(agency);

        employeeRepository.save(employee);
        inviteService.markAsUsed(invite);

        return "employee/employee-registration-success";
    }
}