package br.com.AutoStock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.AutoStock.dto.CepResponse;
import br.com.AutoStock.service.CepApiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CepController {
  private final CepApiService cepService;

  @GetMapping("/cep/{cep}")
  public CepResponse cep(@PathVariable String cep) {
    return cepService.buscarCep(cep);
  }
}

