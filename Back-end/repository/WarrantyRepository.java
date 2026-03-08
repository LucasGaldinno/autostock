package br.com.AutoStock.repository;

import br.com.AutoStock.model.Warranty;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {

    List<Warranty> findByUser(User user);
    
    void deleteByUser(User user);
    
    boolean existsByVehicle(Vehicle vehicle);

    @Query("""
        SELECT w
        FROM Warranty w
        WHERE w.user = :user
          AND w.startDate <= :today
          AND w.endDate >= :today
        """)
    List<Warranty> findActiveWarranties(User user, LocalDate today);

    @Query("""
        SELECT COUNT(w)
        FROM Warranty w
        WHERE w.user.id = :userId
          AND MONTH(w.startDate) = MONTH(CURRENT_DATE)
          AND YEAR(w.startDate) = YEAR(CURRENT_DATE)
    """)
    int countAtivasByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT COUNT(w)
        FROM Warranty w
        WHERE w.user.id = :userId
          AND MONTH(w.startDate) = MONTH(CURRENT_DATE) - 1
          AND YEAR(w.startDate) = YEAR(CURRENT_DATE)
    """)
    int countAtivasMesAnterior(@Param("userId") Long userId);
    
    @Query("""
    	    SELECT COUNT(w)
    	    FROM Warranty w
    	    WHERE w.user = :user
    	      AND w.endDate BETWEEN CURRENT_DATE AND :limitDate
    	""")
    	long countByUserExpiringSoon(@Param("user") User user,
    	                             @Param("limitDate") LocalDate limitDate);
    
    @Query("""
    	    SELECT COUNT(w)
    	    FROM Warranty w
    	    WHERE w.user.id = :userId
    	      AND MONTH(w.startDate) = :month
    	      AND YEAR(w.startDate) = :year
    	""")
    	int countAtivasByMonthAndYear(@Param("userId") Long userId,
    	                              @Param("month") int month,
    	                              @Param("year") int year);


    	@Query("""
    	    SELECT COUNT(w)
    	    FROM Warranty w
    	    WHERE w.user.id = :userId
    	      AND MONTH(w.startDate) = :prevMonth
    	      AND YEAR(w.startDate) = :prevYear
    	""")
    	int countAtivasMesAnterior(@Param("userId") Long userId,
    	                           @Param("prevMonth") int prevMonth,
    	                           @Param("prevYear") int prevYear);

}
