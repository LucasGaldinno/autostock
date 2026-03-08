package br.com.AutoStock.controller;

import br.com.AutoStock.model.User;
import br.com.AutoStock.service.ContractReportService;
import br.com.AutoStock.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
public class ContractReportController {

    @Autowired
    private ContractReportService contractReportService;

    @Autowired
    private UserService userService;

    // ============================================================
    // PAGE VIEW — HTML RETURN
    // ============================================================
    @GetMapping("/contract-reports")
    public String getContractReportsPage(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            Model model) {

        User user = userService.getLoggedUser();
        LocalDate userCreated = user.getCreatedAt().toLocalDate();
        LocalDate today = LocalDate.now();

        String warningMsg = null;

        // ===== Auto-fix dates =====
        if (startDate == null || startDate.isBefore(userCreated)) {
            startDate = userCreated;
            warningMsg = "A data inicial foi ajustada automaticamente.";
        }

        if (endDate == null || endDate.isAfter(today)) {
            endDate = today;
            warningMsg = "A data final foi ajustada automaticamente.";
        }

        if (startDate.isAfter(endDate)) {
            endDate = today;
            warningMsg = "As datas selecionadas eram inválidas e foram ajustadas.";
        }

        // ===== Check if the user has any contract =====
        if (!contractReportService.hasContracts(user.getId())) {
            model.addAttribute("errorMsg", "Você não possui contratos de venda para gerar relatórios.");
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("userCreated", userCreated);
            model.addAttribute("today", today);
            return "contracts-report";
        }

        // ===== Main report =====
        var report = contractReportService.getMonthlyReport(user.getId(), startDate, endDate);
        var brandDistribution = contractReportService.getBrandSalesDistribution(user.getId(), startDate, endDate);

        List<String> labels = report.stream().map(r -> r.getMonthName()).toList();
        List<Double> sales = report.stream().map(r -> r.getTotalSales()).toList();
        List<Double> profits = report.stream().map(r -> r.getTotalProfit()).toList();

        // ===== Chart #3 =====
        var rawMonthly = contractReportService.getMonthlyContractCount(user.getId(), startDate, endDate);
        var normalizedMonthly = contractReportService.normalizeMonthlyContractCount(startDate, endDate, rawMonthly);

        List<String> quantityLabels = new java.util.ArrayList<>(normalizedMonthly.keySet());
        List<Long> quantityData = new java.util.ArrayList<>(normalizedMonthly.values());

        // ===== JSON Serialization =====
        try {
            ObjectMapper mapper = new ObjectMapper();

            model.addAttribute("labelsJson", mapper.writeValueAsString(labels));
            model.addAttribute("salesJson", mapper.writeValueAsString(sales));
            model.addAttribute("profitsJson", mapper.writeValueAsString(profits));

            model.addAttribute("brandLabelsJson", mapper.writeValueAsString(brandDistribution.keySet()));
            model.addAttribute("brandDataJson", mapper.writeValueAsString(brandDistribution.values()));

            model.addAttribute("quantityLabelsJson", mapper.writeValueAsString(quantityLabels));
            model.addAttribute("quantityDataJson", mapper.writeValueAsString(quantityData));

        } catch (Exception e) {
            log.error("Error converting data to JSON: {}", e.getMessage());
        }

        // ===== Additional Page Data =====
        model.addAttribute("report", report);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("userCreated", userCreated);
        model.addAttribute("today", today);

        model.addAttribute("totalSales", contractReportService.getTotalSales(report));
        model.addAttribute("totalProfit", contractReportService.getTotalProfit(report));
        model.addAttribute("totalProfitPercent", contractReportService.getTotalProfitPercentage(report));

        if (warningMsg != null)
            model.addAttribute("warningMsg", warningMsg);

        return "contracts-report";
    }

    // ============================================================
    // API — JSON RETURN
    // ============================================================
    @GetMapping("/contract-reports/api")
    @ResponseBody
    public ResponseEntity<?> getContractReportsApi(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        var userOpt = userService.getUsuarioLogado();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Não autenticado");
        }

        User user = userOpt.get();
        LocalDate userCreated = user.getCreatedAt().toLocalDate();
        LocalDate today = LocalDate.now();

        // auto-fix
        if (startDate == null || startDate.isBefore(userCreated)) startDate = userCreated;
        if (endDate == null || endDate.isAfter(today)) endDate = today;
        if (startDate.isAfter(endDate)) endDate = today;

        try {
            var report = contractReportService.getMonthlyReport(user.getId(), startDate, endDate);
            var brandDistribution = contractReportService.getBrandSalesDistribution(user.getId(), startDate, endDate);

            var rawMonthly = contractReportService.getMonthlyContractCount(user.getId(), startDate, endDate);
            var normalizedMonthly = contractReportService.normalizeMonthlyContractCount(startDate, endDate, rawMonthly);

            HashMap<String, Object> response = new HashMap<>();

            response.put("labels", report.stream().map(r -> r.getMonthName()).toList());
            response.put("sales", report.stream().map(r -> r.getTotalSales()).toList());
            response.put("profits", report.stream().map(r -> r.getTotalProfit()).toList());

            response.put("brandLabels", brandDistribution.keySet());
            response.put("brandData", brandDistribution.values());

            response.put("quantityLabels", normalizedMonthly.keySet());
            response.put("quantityData", normalizedMonthly.values());

            response.put("totalSales", contractReportService.getTotalSales(report));
            response.put("totalProfit", contractReportService.getTotalProfit(report));
            response.put("totalProfitPercent", contractReportService.getTotalProfitPercentage(report));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro ao gerar o relatório: " + e.getMessage());
        }
    }
}
