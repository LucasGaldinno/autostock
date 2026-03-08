package br.com.AutoStock.service;

import org.springframework.stereotype.Service;

import br.com.AutoStock.dto.DashboardKpiDTO;
import br.com.AutoStock.repository.ContractRepository;
import br.com.AutoStock.repository.VehicleRepository;
import br.com.AutoStock.repository.WarrantyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VehicleRepository vehicleRepository;
    private final ContractRepository contractRepository;
    private final WarrantyRepository warrantyRepository;

    public DashboardKpiDTO calcularKpis(Long userId) {

        // Dados atuais
        int veiculosAtivos = vehicleRepository.countByUserId(userId);
        int contratosMes = contractRepository.countContratosMesAtual(userId);
        int garantiasAtivas = warrantyRepository.countAtivasByUserId(userId);

        // Dados do mês anterior
        int veiculosAnterior = vehicleRepository.countByUserIdMesAnterior(userId);
        int contratosAnterior = contractRepository.countContratosMesAnterior(userId);
        int garantiasAnterior = warrantyRepository.countAtivasMesAnterior(userId);

        // Variações individuais
        double varVeiculos = calcularVariacao(veiculosAtivos, veiculosAnterior);
        double varContratos = calcularVariacao(contratosMes, contratosAnterior);
        double varGarantias = calcularVariacao(garantiasAtivas, garantiasAnterior);

        // Crescimento real (média das 3 variações)
        double crescimento = (varVeiculos + varContratos + varGarantias) / 3;

        return new DashboardKpiDTO(
                veiculosAtivos,
                contratosMes,
                garantiasAtivas,
                varVeiculos,
                varContratos,
                varGarantias,
                crescimento
        );
    }

    private double calcularVariacao(int atual, int anterior) {
        if (anterior == 0 && atual > 0) return 100.0;
        if (anterior == 0) return 0.0;
        return ((double)(atual - anterior) / anterior) * 100.0;
    }
}
