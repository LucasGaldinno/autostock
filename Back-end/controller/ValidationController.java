package br.com.AutoStock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.AutoStock.repository.IUserService;

@RestController
@RequestMapping("/api/validate")
@RequiredArgsConstructor
public class ValidationController {

    private final IUserService userService;

    @GetMapping("/email")
    public ResponseEntity<Boolean> validateEmail(@RequestParam("email") String email) {
        boolean exists = userService.findByEmail(email).isPresent();
        return ResponseEntity.ok(exists);
    }
}
