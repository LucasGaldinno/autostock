package br.com.AutoStock.controller;

import br.com.AutoStock.dto.UpdateProfileRequest;
import br.com.AutoStock.model.User;
import br.com.AutoStock.service.ProfileService;
import br.com.AutoStock.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;

    @GetMapping
    public String profilePage(Model model) {
        User user = userService.getUsuarioLogado().orElseThrow();

        UpdateProfileRequest dto = new UpdateProfileRequest();
        dto.setNomeFantasia(user.getNomeFantasia());
        dto.setTelefone(user.getTelefone());
        dto.setCep(user.getCep());
        dto.setLogradouro(user.getLogradouro());
        dto.setNumero(user.getNumero());
        dto.setComplemento(user.getComplemento());
        dto.setBairro(user.getBairro());
        dto.setCidade(user.getCidade());
        dto.setUf(user.getUf());

        model.addAttribute("user", user);
        model.addAttribute("form", dto);

        return "profile";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("form") UpdateProfileRequest form,
                         BindingResult result,
                         Model model) {

        User user = userService.getUsuarioLogado().orElseThrow();

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "profile";
        }

        profileService.updateProfile(user, form);
        model.addAttribute("user", user);
        model.addAttribute("success", true);

        return "redirect:/home?updated=true";
    }

    @PostMapping("/delete")
    public String deleteAccount(RedirectAttributes redirect) {

        var userOpt = userService.getUsuarioLogado();

        if (userOpt.isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/profile";
        }

        var user = userOpt.get();

        try {
            userService.deleteUser(user);
            redirect.addFlashAttribute("successMessage", "Sua conta foi excluída com sucesso.");
            return "redirect:/login";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erro ao excluir conta. Tente novamente.");
            return "redirect:/profile";
        }
    }
}
