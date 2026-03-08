package br.com.AutoStock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.AutoStock.event.RegistrationCompleteEventListener;
import br.com.AutoStock.model.SaleContract;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Vehicle;
import br.com.AutoStock.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ContractController {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final SaleContractService saleContractService;
    private final RegistrationCompleteEventListener registrationCompleteEventListener;

    @GetMapping("/contracts/create")
    public String createForm(Model model) {
        var userOpt = userService.getUsuarioLogado();
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        var vehicles = vehicleService.getVehiclesInStock(user);

        if (vehicles.isEmpty()) {
            model.addAttribute("errorMessage", "Você não possui veículos disponíveis no estoque para gerar contrato.");
            return "redirect:/home";
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("contract", new SaleContract());
        return "contract-create";
    }

    @PostMapping("/contracts/generate")
    public String generateContract(
            @ModelAttribute("contract") SaleContract contract,
            @RequestParam(value = "agencySignatureFile", required = false) MultipartFile agencySignatureFile,
            @RequestParam(value = "customerSignatureFile", required = false) MultipartFile customerSignatureFile,
            RedirectAttributes redirectAttributes) {

        var userOpt = userService.getUsuarioLogado();
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
            return "redirect:/login";
        }

        User user = userOpt.get();

	     // ==================== VALIDAÇÃO DO E-MAIL DO CLIENTE ====================
	
	     String customerEmail = contract.getCustomerEmail();
	
	     // 1. Bloqueia se o e-mail do cliente for igual ao e-mail da agência
	     if (customerEmail != null && customerEmail.equalsIgnoreCase(user.getEmail())) {
	         redirectAttributes.addFlashAttribute(
	                 "errorMessage",
	                 "O e-mail informado pertence à agência. Informe o e-mail real do cliente."
	         );
	         return "redirect:/contracts/create";
	     }
	
	     // 2. Bloqueia se o e-mail já existir no sistema (funcionário ou outro usuário)
	     if (userService.existsByEmail(customerEmail)) {
	         redirectAttributes.addFlashAttribute(
	                 "errorMessage",
	                 "O e-mail informado já pertence a um usuário do sistema. Informe o e-mail real do cliente."
	         );
	         return "redirect:/contracts/create";
	     }

        try {
            // ===== 1. Gera contrato + garantia =====
            SaleContractService.ContractResult result =
                    saleContractService.createContract(contract, user,
                            agencySignatureFile != null ? agencySignatureFile.getBytes() : null,
                            customerSignatureFile != null ? customerSignatureFile.getBytes() : null);

            byte[] contratoPdf = result.getContractPdf();
            byte[] garantiaPdf = result.getWarrantyPdf();
            Vehicle vehicle = result.getSaleContract().getVehicle();

            String vehicleInfo = vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlate() + ")";

            // ===== 2. Envia e-mails =====
            if (contract.getCustomerEmail() != null && !contract.getCustomerEmail().isBlank()) {
                registrationCompleteEventListener.sendContractAndWarrantyToCustomer(
                        contract.getCustomerEmail(),
                        contract.getCustomerName(),
                        contratoPdf,
                        garantiaPdf,
                        vehicleInfo
                );
            }

            // Envia cópia para a agência
            registrationCompleteEventListener.sendContractAndWarrantyToCustomer(
                    user.getEmail(),
                    user.getRazaoSocial(),
                    contratoPdf,
                    garantiaPdf,
                    vehicleInfo
            );

            // ===== 3. Mensagem de sucesso =====
            redirectAttributes.addFlashAttribute("successMessage", "✅ Contrato gerado com sucesso!");
            return "redirect:/home";

        } catch (Exception e) {
            log.error("Erro ao gerar contrato: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao gerar contrato: " + e.getMessage());
            return "redirect:/contracts/create";
        }
    }
}
