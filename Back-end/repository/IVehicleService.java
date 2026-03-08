package br.com.AutoStock.repository;

import java.util.List;
import java.util.Optional;

import br.com.AutoStock.model.Vehicle;

public interface IVehicleService {
    List<Vehicle> getAllVehicles();

    Optional<Vehicle> getVehicleById(Long vehicleId);

    Vehicle createVehicle(Vehicle vehicle);

    Vehicle updateVehicle(Vehicle vehicle);

    void deleteVehicle(Long vehicleId);
}
