package br.com.AutoStock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.AutoStock.model.User;
import br.com.AutoStock.model.VehicleImage;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
	
	void deleteByVehicle_User(User user);
	
}
