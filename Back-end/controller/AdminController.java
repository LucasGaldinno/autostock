package br.com.AutoStock.controller;

import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.UserRepository;
import br.com.AutoStock.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserRepository userRepository;
	private final UserService userService;

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        long totalUsers = userRepository.count();
        long lockedUsers = userRepository.countByAccountLockedTrue();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("lockedUsers", lockedUsers);

        return "admin/dashboard";
    }

    // Listar todos os usuários
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "admin/users";
    }

    // Listar usuários bloqueados
    @GetMapping("/bloqueados")
    public String listarBloqueados(Model model) {

        List<User> users = userRepository.findByAccountLockedTrue();
        model.addAttribute("users", users);

        return "admin/locked-users"; 
    }

    // Desbloquear usuário
    @PostMapping("/desbloquear/{id}")
    public String desbloquear(@PathVariable Long id) {

        userRepository.findById(id).ifPresent(user -> {
            user.setAccountLocked(false);
            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        });

        return "redirect:/admin/bloqueados";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        userService.deleteUser(user); // ✅ agora chama a limpeza completa

        return "redirect:/admin/usuarios?excluido=1";
    }

    // Formulário de edição
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        model.addAttribute("user", user);

        return "admin/edit-user";
    }

    // Salvar alterações
    @PostMapping("/editar/{id}")
    public String salvarEdicao(@PathVariable Long id,
                               @ModelAttribute("user") User updatedUser) {

        userRepository.findById(id).ifPresent(user -> {

            user.setEmail(updatedUser.getEmail());
            user.setTelefone(updatedUser.getTelefone());

            user.setCnpj(updatedUser.getCnpj());
            user.setRazaoSocial(updatedUser.getRazaoSocial());
            user.setNomeFantasia(updatedUser.getNomeFantasia());
            user.setInscricaoEstadual(updatedUser.getInscricaoEstadual());

            user.setCep(updatedUser.getCep());
            user.setLogradouro(updatedUser.getLogradouro());
            user.setNumero(updatedUser.getNumero());
            user.setComplemento(updatedUser.getComplemento());
            user.setBairro(updatedUser.getBairro());
            user.setCidade(updatedUser.getCidade());
            user.setUf(updatedUser.getUf());

            userRepository.save(user);
        });

        return "redirect:/admin/usuarios";
    }
}
