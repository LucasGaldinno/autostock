package br.com.AutoStock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.AutoStock.model.SaleContract;
import br.com.AutoStock.model.User;

@Repository
public interface SaleContractRepository extends JpaRepository<SaleContract, Long> {

    // 🔍 Garante que um veículo não tenha dois contratos
    boolean existsByVehicleId(Long vehicleId);
    
    void deleteByUser(User user);

    // (opcional) caso queira buscar contrato pelo ID do veículo
    @Query("SELECT c FROM SaleContract c WHERE c.vehicle.id = :vehicleId")
    SaleContract findByVehicleId(Long vehicleId);
}
