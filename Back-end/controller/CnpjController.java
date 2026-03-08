package br.com.AutoStock.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.AutoStock.dto.CnpjResponse;
import br.com.AutoStock.service.CnpjApiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cnpj")
@RequiredArgsConstructor
public class CnpjController {

    private final CnpjApiService brasilApiService;

    @GetMapping("/{cnpj}")
    public ResponseEntity<?> consultarCnpj(@PathVariable String cnpj) {
        try {
            CnpjResponse response = brasilApiService.consultarCnpj(cnpj);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao consultar CNPJ.");
        }
    }
}
