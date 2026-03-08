package br.com.AutoStock.controller;

import br.com.AutoStock.model.User;
import br.com.AutoStock.service.UserService;
import br.com.AutoStock.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WarrantyController {

    private final UserService userService;
    private final WarrantyService warrantyService;

    @GetMapping("/warranties")
    public String listAll(Model model) {
        var userOpt = userService.getUsuarioLogado();
        if (userOpt.isEmpty()) {
            log.warn("Acesso não autorizado à página de garantias — redirecionando para login.");
            return "redirect:/login";
        }

        User user = userOpt.get();

        var activeWarranties = warrantyService.getActiveWarranties(user);
        model.addAttribute("activeWarranties", activeWarranties);

        var allWarranties = warrantyService.getAllByUser(user);
        model.addAttribute("warranties", allWarranties);

        if (allWarranties.isEmpty()) {
            model.addAttribute("message", "Você ainda não possui garantias registradas.");
        }

        log.info("Usuário {} acessou a listagem de garantias (ativas: {}, total: {}).",
                user.getEmail(), activeWarranties.size(), allWarranties.size());
        
        long activeCount = warrantyService.countActiveWarranties(user);
        long expiringSoonCount = warrantyService.countExpiringWarranties(user, LocalDate.now().plusDays(30));
        long expiredCount = warrantyService.countExpiredWarranties(user);

        model.addAttribute("activeCount", activeCount);
        model.addAttribute("expiringSoonCount", expiringSoonCount);
        model.addAttribute("expiredCount", expiredCount);

        log.info("Garantias do usuário {} — Ativas: {}, Expirando: {}, Expiradas: {}, Total: {}",
                user.getEmail(),
                activeCount,
                expiringSoonCount,
                expiredCount,
                allWarranties.size());

        return "warranty-list";
    }
}
