package br.com.AutoStock.repository;

import br.com.AutoStock.model.SaleContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<SaleContract, Long> {

    // ============================================================
    // MONTHLY SALES + PROFIT REPORT
    // ============================================================
    @Query(value = """
        SELECT 
            MONTH(sc.created_at) AS month,
            SUM(sc.sale_price) AS total_sales,
            SUM(sc.sale_price - v.purchase_price) AS total_profit
        FROM sale_contracts sc
        JOIN vehicle v ON v.id = sc.vehicle_id
        WHERE sc.user_id = :userId
          AND sc.created_at BETWEEN :startDate AND :endDate
        GROUP BY MONTH(sc.created_at)
        ORDER BY MONTH(sc.created_at)
    """, nativeQuery = true)
    List<Object[]> findMonthlyContractReport(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ============================================================
    // BRAND SALES DISTRIBUTION (FILTERED BY DATE)
    // ============================================================
    @Query(value = """
        SELECT 
            v.brand AS brand,
            COUNT(*) AS total
        FROM sale_contracts sc
        JOIN vehicle v ON v.id = sc.vehicle_id
        WHERE sc.user_id = :userId
          AND sc.created_at BETWEEN :startDate AND :endDate
        GROUP BY v.brand
        ORDER BY total DESC
    """, nativeQuery = true)
    List<Object[]> findBrandSalesDistribution(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ============================================================
    // EXISTENCE CHECKS
    // ============================================================
    boolean existsByUser_Id(Long userId);

    boolean existsByUser_IdAndCreatedAtBetween(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // ============================================================
    // MONTHLY CONTRACT COUNT (FOR CHART #3)
    // ============================================================
    @Query("""
        SELECT 
            MONTH(sc.createdAt) AS month,
            COUNT(*) AS total
        FROM SaleContract sc
        WHERE sc.user.id = :userId
          AND sc.createdAt BETWEEN :startDate AND :endDate
        GROUP BY MONTH(sc.createdAt)
        ORDER BY MONTH(sc.createdAt)
    """)
    List<Object[]> findSalesCountByMonth(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

 // Optional for dashboards
    long countByUser_Id(Long userId);
    
 // Contratos do mês atual
    @Query("""
        SELECT COUNT(c)
        FROM SaleContract c
        WHERE c.user.id = :userId
          AND MONTH(c.createdAt) = MONTH(CURRENT_DATE)
          AND YEAR(c.createdAt) = YEAR(CURRENT_DATE)
    """)
    int countContratosMesAtual(@Param("userId") Long userId);

    // Contratos do mês anterior
    @Query("""
        SELECT COUNT(c)
        FROM SaleContract c
        WHERE c.user.id = :userId
          AND MONTH(c.createdAt) = MONTH(CURRENT_DATE) - 1
          AND YEAR(c.createdAt) = YEAR(CURRENT_DATE)
    """)
    int countContratosMesAnterior(@Param("userId") Long userId);

}
