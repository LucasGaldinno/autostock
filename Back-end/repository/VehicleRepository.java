package br.com.AutoStock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.AutoStock.model.Vehicle;
import br.com.AutoStock.model.User;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // ====== LISTAGEM PADRÃO ======
	List<Vehicle> findByUserAndAvailableTrue(User user);
    
    @Query("SELECT v FROM Vehicle v LEFT JOIN FETCH v.images WHERE v.user = :user")
    List<Vehicle> findByUserWithImages(@Param("user") User user);
    
    @Query("SELECT v FROM Vehicle v LEFT JOIN FETCH v.images WHERE v.id = :id")
    Optional<Vehicle> findByIdWithImages(@Param("id") Long id);
    
    // ====== VERIFICAÇÕES DE DUPLICIDADE ======
    boolean existsByPlate(String plate);
    boolean existsByRenavam(String renavam);
    boolean existsByChassis(String chassis);
    
    Optional<Vehicle> findByPlate(String plate);
    Optional<Vehicle> findByRenavam(String renavam);
    Optional<Vehicle> findByChassis(String chassis);

    @Query("""
    	    SELECT COUNT(v)
    	    FROM Vehicle v
    	    WHERE v.user.id = :userId
    	      AND MONTH(v.createdAt) = MONTH(CURRENT_DATE)
    	      AND YEAR(v.createdAt) = YEAR(CURRENT_DATE)
    	""")
    	int countByUserId(@Param("userId") Long userId);

    	@Query("""
    	    SELECT COUNT(v)
    	    FROM Vehicle v
    	    WHERE v.user.id = :userId
    	      AND MONTH(v.createdAt) = MONTH(CURRENT_DATE) - 1
    	      AND YEAR(v.createdAt) = YEAR(CURRENT_DATE)
    	""")
    	int countByUserIdMesAnterior(@Param("userId") Long userId);

	void deleteByUser(User user);

	@Query("""
		    SELECT v FROM Vehicle v
		    LEFT JOIN FETCH v.images
		    WHERE v.user = :user
		      AND v.available = true
		""")
		List<Vehicle> findInStockByUser(@Param("user") User user);
}
