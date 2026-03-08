package br.com.AutoStock.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.AutoStock.model.SaleContract;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Vehicle;
import br.com.AutoStock.model.Warranty;
import br.com.AutoStock.repository.SaleContractRepository;
import br.com.AutoStock.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleContractService {

    private final SaleContractRepository saleContractRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;
    private final ContractPdfService contractPdfService;
    private final WarrantyService warrantyService;

    /**
     * Cria um contrato de venda completo, gera o PDF e cria a garantia padrão.
     * Retorna um objeto com os bytes do contrato e da garantia.
     */
    @Transactional
    public ContractResult createContract(SaleContract contract,
                                         User user,
                                         byte[] assinaturaAgencia,
                                         byte[] assinaturaCliente) {

        // ======= 1. Valida duplicidade =======
        if (contract.getVehicle() == null || contract.getVehicle().getId() == null) {
            throw new IllegalArgumentException("Veículo não informado.");
        }

        Long vehicleId = contract.getVehicle().getId();
        if (saleContractRepository.existsByVehicleId(vehicleId)) {
            throw new IllegalStateException("Este veículo já possui um contrato gerado.");
        }

        // ======= 2. Busca o veículo completo =======
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado no banco."));
        contract.setVehicle(vehicle);

        // ======= 3. Preenche dados básicos =======
        contract.setUser(user);
        contract.setCreatedAt(LocalDateTime.now());

        // ======= 4. Salva contrato =======
        SaleContract saved = saleContractRepository.save(contract);
        log.info("Contrato de venda salvo com ID {} para o veículo {}", saved.getId(), vehicle.getPlate());

        // 🔹 Marca o veículo como vendido
        Vehicle vehicleToUpdate = saved.getVehicle();
        vehicleToUpdate.setAvailable(false);
        vehicleService.markAsSold(vehicleToUpdate);
        log.info("✅ Veículo {} marcado como vendido.", vehicleToUpdate.getPlate());

        // ======= 5. Gera PDF do contrato =======
        byte[] contractPdf = contractPdfService.generateContractPdf(saved, assinaturaAgencia, assinaturaCliente);
        log.info("PDF do contrato gerado ({} bytes)", contractPdf.length);

        // ======= 6. Cria e gera garantia padrão =======
        Warranty warranty = warrantyService.createDefaultWarranty(saved);
        byte[] warrantyPdf = warrantyService.generateWarrantyPdf(warranty);
        log.info("PDF da garantia gerado ({} bytes)", warrantyPdf.length);

        // ======= 7. Retorna ambos os PDFs =======
        return new ContractResult(contractPdf, warrantyPdf, saved, warranty);
    }

    /**
     * Classe auxiliar que encapsula os PDFs gerados e as entidades persistidas.
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class ContractResult {
        private final byte[] contractPdf;
        private final byte[] warrantyPdf;
        private final SaleContract saleContract;
        private final Warranty warranty;
    }
}
