package br.com.AutoStock.controller;

import br.com.AutoStock.service.DashboardService;
import br.com.AutoStock.service.UserService;
import br.com.AutoStock.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MappingController {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final DashboardService dashboardService;

    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(value = "updated", required = false) String updated) {

        var user = userService.getUsuarioLogado().orElse(null);

        boolean temVeiculos = false;

        if (user != null) {

            Long userId = user.getId();

            // Nome exibido
            String nomeExibicao = (user.getNomeFantasia() != null && !user.getNomeFantasia().isBlank())
                    ? user.getNomeFantasia()
                    : user.getRazaoSocial();

            model.addAttribute("usuarioNome", nomeExibicao);

            // Verifica se tem veículos
            temVeiculos = !vehicleService.getVehiclesByUser(user).isEmpty();
            model.addAttribute("temVeiculos", temVeiculos);

            // 👉 CARREGAR KPI REAL
            model.addAttribute("kpis", dashboardService.calcularKpis(userId));

        } else {
            log.warn("Nenhum usuário logado encontrado!");
        }

        // Aviso de perfil atualizado
        if (updated != null) {
            model.addAttribute("perfilAtualizado", true);
        }

        return "home";
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
    
    @GetMapping("/terms")
    public String termos() {
        return "terms";
    }
}
