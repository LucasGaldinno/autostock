package br.com.AutoStock.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.AutoStock.dto.FipeResponse;
import br.com.AutoStock.service.FipeApiService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/fipe")
@RequiredArgsConstructor
public class FipeController {

    private final FipeApiService fipeApiService;

    @GetMapping("/{codigoFipe}")
    public ResponseEntity<?> consultarFipe(@PathVariable String codigoFipe) {
        try {
            List<FipeResponse> response = fipeApiService.consultarPreco(codigoFipe);

            if (response == null || response.isEmpty()) {
                return ResponseEntity.badRequest().body("Código FIPE não encontrado.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao consultar FIPE.");
        }
    }
}
