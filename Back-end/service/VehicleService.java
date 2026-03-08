package br.com.AutoStock.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.AutoStock.exception.DuplicateVehicleException;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Vehicle;
import br.com.AutoStock.repository.VehicleImageRepository;
import br.com.AutoStock.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleImageRepository imageRepository;

    public List<Vehicle> getVehiclesByUser(User user) {
        return vehicleRepository.findByUserWithImages(user);
    }
    
    public List<Vehicle> getVehiclesInStock(User user) {
        return vehicleRepository.findInStockByUser(user);
    }
    
    @Transactional
    public void markAsSold(Vehicle vehicle) {
        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> findById(Long id) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByIdWithImages(id);
        vehicleOptional.ifPresent(vehicle -> {
            // Se não houver imagens associadas, garantir que a lista seja inicializada
            if (vehicle.getImages() == null) {
                vehicle.setImages(new ArrayList<>());  // Garantir que a lista não seja null
            }
        });
        return vehicleOptional;
    }


    public Vehicle createVehicle(Vehicle vehicle) {
    	normalizeVehicle(vehicle);
    	 if (vehicle.getImages() == null) {
    	        vehicle.setImages(new ArrayList<>()); // Garante que a lista de imagens nunca seja null
    	    }

    	validarDuplicidade(vehicle); 
        return vehicleRepository.save(vehicle);
    }

    private void validarDuplicidade(Vehicle vehicle) {
        if (vehicleRepository.existsByPlate(vehicle.getPlate())) {
            throw new DuplicateVehicleException("Já existe um veículo cadastrado com esta placa.");
        }

        if (vehicleRepository.existsByRenavam(vehicle.getRenavam())) {
            throw new DuplicateVehicleException("Já existe um veículo cadastrado com este RENAVAM.");
        }

        if (vehicleRepository.existsByChassis(vehicle.getChassis())) {
            throw new DuplicateVehicleException("Já existe um veículo cadastrado com este chassi.");
        }
    }

	public Vehicle updateVehicle(Vehicle vehicle) {
	    log.debug("Atualizando veículo ID={} | Marca={} | Modelo={} | Placa={} | Versão={} | Combustível={} | Câmbio={} | Portas={}",
	            vehicle.getId(),
	            vehicle.getBrand(),
	            vehicle.getModel(),
	            vehicle.getPlate(),
	            vehicle.getVersion(),
	            vehicle.getFuel(),
	            vehicle.getTransmission(),
	            vehicle.getDoors()
	    );

	    normalizeVehicle(vehicle);

	    vehicleRepository.findByPlate(vehicle.getPlate()).ifPresent(v -> {
	        if (!v.getId().equals(vehicle.getId())) {
	            throw new DuplicateVehicleException("Já existe um veículo cadastrado com esta placa.");
	        }
	    });

	    vehicleRepository.findByRenavam(vehicle.getRenavam()).ifPresent(v -> {
	        if (!v.getId().equals(vehicle.getId())) {
	            throw new DuplicateVehicleException("Já existe um veículo cadastrado com este RENAVAM.");
	        }
	    });

	    vehicleRepository.findByChassis(vehicle.getChassis()).ifPresent(v -> {
	        if (!v.getId().equals(vehicle.getId())) {
	            throw new DuplicateVehicleException("Já existe um veículo cadastrado com este chassi.");
	        }
	    });

	    return vehicleRepository.save(vehicle);
	}

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public boolean isVeiculoDoUsuario(Vehicle vehicle, User usuarioLogado) {
        if (vehicle == null || usuarioLogado == null || vehicle.getUser() == null) return false;
        return vehicle.getUser().getId().equals(usuarioLogado.getId());
    }

    @Transactional
    public void deleteImage(Long imageId) {

        imageRepository.findById(imageId).ifPresent(img -> {

            // 1 — Remover da lista do veículo (fundamental!)
            Vehicle vehicle = img.getVehicle();
            if (vehicle != null && vehicle.getImages() != null) {
                vehicle.getImages().remove(img);
            }

            // 2 — Remover arquivo físico
            try {
                Path uploadPath = Paths.get("./uploads").resolve(img.getImageUrl());
                Files.deleteIfExists(uploadPath);
                log.info("Arquivo físico removido: {}", uploadPath.toAbsolutePath());
            } catch (Exception e) {
                log.error("Erro ao deletar arquivo físico da imagem {}: {}", img.getImageUrl(), e.getMessage(), e);
            }

            // 3 — Remover do banco
            imageRepository.delete(img);
            log.info("Imagem ID {} removida do banco", imageId);
        });
    }
    
    private void normalizeVehicle(Vehicle vehicle) {
        vehicle.setPlate(normalizePlate(vehicle.getPlate()));
        vehicle.setChassis(normalizeChassis(vehicle.getChassis()));
        vehicle.setRenavam(normalizeRenavam(vehicle.getRenavam()));

        if (vehicle.getVersion() != null) {
            vehicle.setVersion(vehicle.getVersion().trim());
        }
        if (vehicle.getAdditionalInfo() != null) {
            vehicle.setAdditionalInfo(vehicle.getAdditionalInfo().trim());
        }
        if (vehicle.getManufactureYear() == null || vehicle.getModelYear() == null) {
            log.warn("Veículo ID={} sem ano definido corretamente", vehicle.getId());
        }
    }

    private String normalizePlate(String plate) {
        if (plate == null) return null;
        String p = plate.toUpperCase().replaceAll("[^A-Z0-9-]", "").trim();

        if (p.matches("^[A-Z]{3}[0-9]{4}$")) {
            return p.substring(0, 3) + "-" + p.substring(3);
        }
        return p;
    }

    private String normalizeRenavam(String renavam) {
        if (renavam == null) return null;
        String digits = renavam.replaceAll("\\D", "");
        if (digits.length() > 11) digits = digits.substring(0, 11);
        if (digits.length() < 11) digits = String.format("%11s", digits).replace(' ', '0');
        return digits;
    }

    private String normalizeChassis(String chassis) {
        if (chassis == null) return null;
        return chassis.toUpperCase().replaceAll("[^A-HJ-NPR-Z0-9]", "").trim();
    }

}
