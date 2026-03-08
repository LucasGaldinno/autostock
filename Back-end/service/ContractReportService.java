package br.com.AutoStock.service;

import br.com.AutoStock.dto.MonthlyContractReportDTO;
import br.com.AutoStock.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContractReportService {

    private final ContractRepository contractRepository;

    // ============================================================
    // MAIN REPORT (Sales + Profit per Month)
    // ============================================================
    public List<MonthlyContractReportDTO> getMonthlyReport(Long userId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Object[]> results = contractRepository.findMonthlyContractReport(userId, start, end);

        return results.stream()
                .map(row -> new MonthlyContractReportDTO(
                        ((Number) row[0]).intValue(),      // month
                        ((Number) row[1]).doubleValue(),   // total sales
                        ((Number) row[2]).doubleValue()    // total profit
                ))
                .toList();
    }

    // ============================================================
    // CHECKS
    // ============================================================
    public boolean hasContracts(Long userId) {
        return contractRepository.existsByUser_Id(userId);
    }

    public boolean hasContracts(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return contractRepository.existsByUser_IdAndCreatedAtBetween(userId, start, end);
    }

    // ============================================================
    // METRICS
    // ============================================================
    public double getTotalSales(List<MonthlyContractReportDTO> list) {
        return list.stream().mapToDouble(MonthlyContractReportDTO::getTotalSales).sum();
    }

    public double getTotalProfit(List<MonthlyContractReportDTO> list) {
        return list.stream().mapToDouble(MonthlyContractReportDTO::getTotalProfit).sum();
    }

    public double getTotalProfitPercentage(List<MonthlyContractReportDTO> list) {
        double totalSales = getTotalSales(list);
        double totalProfit = getTotalProfit(list);
        return totalSales > 0 ? (totalProfit / totalSales) * 100 : 0;
    }

    // ============================================================
    // SALES DISTRIBUTION BY BRAND
    // ============================================================
    public Map<String, Long> getBrandSalesDistribution(Long userId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Object[]> results = contractRepository.findBrandSalesDistribution(userId, start, end);

        Map<String, Long> distribution = new LinkedHashMap<>();

        for (Object[] row : results) {
            String brand = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            distribution.put(brand, count);
        }

        return distribution;
    }

    // ============================================================
    // CHART #3 — MONTHLY CONTRACT COUNT
    // ============================================================
    public Map<Integer, Long> getMonthlyContractCount(Long userId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = contractRepository.findSalesCountByMonth(userId, start, end);

        Map<Integer, Long> map = new LinkedHashMap<>();

        for (Object[] row : results) {
            Integer month = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            map.put(month, count);
        }

        return map;
    }

    // Month abbreviation formatter (en-US)
    private String formatMonth(LocalDate date) {
        return switch (date.getMonthValue()) {
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "?";
        };
    }

    // Normalizes month range by filling empty months with zero
    public Map<String, Long> normalizeMonthlyContractCount(LocalDate startDate, LocalDate endDate, Map<Integer, Long> rawData) {

        Map<String, Long> normalized = new LinkedHashMap<>();

        LocalDate cursor = startDate.withDayOfMonth(1);
        LocalDate limit = endDate.withDayOfMonth(1);

        while (!cursor.isAfter(limit)) {

            int month = cursor.getMonthValue();
            String label = formatMonth(cursor);

            Long value = rawData.getOrDefault(month, 0L);

            normalized.put(label, value);

            cursor = cursor.plusMonths(1);
        }

        return normalized;
    }
}
