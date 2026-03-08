package br.com.AutoStock.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import br.com.AutoStock.model.SaleContract;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Vehicle;
import br.com.AutoStock.model.Warranty;
import br.com.AutoStock.repository.VehicleRepository;
import br.com.AutoStock.repository.WarrantyRepository;

@Service
@RequiredArgsConstructor
public class WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final VehicleRepository vehicleRepository; 
    private final WarrantyPdfService warrantyPdfService;

    public Warranty createDefaultWarranty(SaleContract contract) {
        Vehicle vehicle = contract.getVehicle();
        User user = contract.getUser();

        if (vehicle == null || vehicle.getId() == null) {
            throw new IllegalArgumentException("Veículo não informado para criar garantia.");
        }

        vehicleRepository.findById(vehicle.getId())
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado ao criar garantia."));

        if (warrantyRepository.existsByVehicle(vehicle)) {
            return warrantyRepository.findByUser(user).stream()
                    .filter(w -> w.getVehicle().getId().equals(vehicle.getId()))
                    .findFirst()
                    .orElse(null);
        }

        Warranty warranty = new Warranty();
        warranty.setContract(contract);
        warranty.setVehicle(vehicle);
        warranty.setUser(user);
        warranty.setStartDate(LocalDate.now());
        warranty.setEndDate(LocalDate.now().plusMonths(3));
        warranty.setDescription("Garantia padrão de 3 meses a partir da data do contrato.");

        return warrantyRepository.save(warranty);
    }

    public byte[] generateWarrantyPdf(Warranty warranty) {
        return warrantyPdfService.generateWarrantyPdf(warranty);
    }

    public List<Warranty> getActiveWarranties(User user) {
    	updateWarrantyStatus();  
        return warrantyRepository.findActiveWarranties(user, LocalDate.now());
    }

    public List<Warranty> getAllByUser(User user) {
    	updateWarrantyStatus();  
        return warrantyRepository.findByUser(user);
    }

    private void updateWarrantyStatus() {
        List<Warranty> warranties = warrantyRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Warranty warranty : warranties) {
            if (warranty.getEndDate().isBefore(today)) {
                warranty.setExpired(true); 
            } else {
                warranty.setExpired(false); 
            }

            warrantyRepository.save(warranty);
        }
    }

    public long countExpiringSoonByUser(User user) {
    	LocalDate limit = LocalDate.now().plusDays(30);
    	long count = warrantyRepository.countByUserExpiringSoon(user, limit);
		return count;
    }

    public int countAtivasMesAtual(Long userId) {
        LocalDate hoje = LocalDate.now();
        return warrantyRepository.countAtivasByMonthAndYear(
                userId,
                hoje.getMonthValue(),
                hoje.getYear()
        );
    }

    public int countAtivasMesAnterior(Long userId) {
        LocalDate hoje = LocalDate.now();
        LocalDate mesAnterior = hoje.minusMonths(1);

        return warrantyRepository.countAtivasMesAnterior(
                userId,
                mesAnterior.getMonthValue(),
                mesAnterior.getYear()
        );
    }
    
    public long countActiveWarranties(User user) {
        return getActiveWarranties(user).size();
    }

    public long countExpiredWarranties(User user) {
        return getAllByUser(user).stream().filter(Warranty::isExpired).count();
    }

    public long countExpiringWarranties(User user, LocalDate limit) {
        return countExpiringSoonByUser(user);
    }


}
