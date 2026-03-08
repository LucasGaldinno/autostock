package br.com.AutoStock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import br.com.AutoStock.service.EmployeeInviteService;
import br.com.AutoStock.service.UserService;
import br.com.AutoStock.model.User;

@Controller
@RequestMapping("/funcionario")
@RequiredArgsConstructor
public class EmployeeInviteController {

    private final EmployeeInviteService inviteService;
    private final UserService userService;

    // Exibe página para enviar convite
    @GetMapping("/convite")
    public String showInvitePage() {
        return "employee/employee-invite";
    }

    @PostMapping("/convite")
    public String sendInvite(
            @RequestParam("email") String email,
            Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedEmail = auth.getName();

        User agency = userService.findByEmail(loggedEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (userService.findByEmail(email).isPresent()) {
            model.addAttribute("toastError", "Este e-mail já está cadastrado no sistema.");
            return "employee/employee-invite";
        }

        if (userService.existsEmployeeByEmail(email)) {
            model.addAttribute("toastError", "Já existe um funcionário com este e-mail.");
            return "employee/employee-invite";
        }

        try {
            inviteService.generateInvite(agency, email);

            model.addAttribute("toastSuccess", "Convite enviado com sucesso!");
            return "employee/employee-invite";

        } catch (RuntimeException e) {
            model.addAttribute("toastError", e.getMessage());
            return "employee/employee-invite";
        }
    }
}
