package br.com.AutoStock.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import br.com.AutoStock.dto.FipeResponse;
import br.com.AutoStock.exception.FipeCodigoInvalidoException;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Vehicle;
import br.com.AutoStock.model.VehicleImage;
import br.com.AutoStock.service.FipeApiService;
import br.com.AutoStock.service.UserService;
import br.com.AutoStock.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final UserService userService;
    private final FipeApiService fipeApiService;

    @GetMapping
    public String getVehicles(Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOptional = userService.getUsuarioLogado();

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                log.info("Usuário logado: id={} email={}", user.getId(), user.getEmail());

                List<Vehicle> vehicles = vehicleService.getVehiclesInStock(user);
                log.info("Veículos encontrados: {}", vehicles.size());

                model.addAttribute("vehicles", vehicles);
                
                double totalEstoque = vehicles.stream()
                	    .filter(v -> v.getPurchasePrice() != null)
                	    .mapToDouble(Vehicle::getPurchasePrice)
                	    .sum();

                model.addAttribute("totalEstoque", totalEstoque);
            } else {
                log.warn("Nenhum usuário logado!");
                model.addAttribute("vehicles", List.of());
            }

            model.addAllAttributes(redirectAttributes.getFlashAttributes());
            return "vehicles";

        } catch (Exception e) {
            log.error("Erro ao carregar veículos", e);
            model.addAttribute("errorMessage", "Erro ao carregar veículos: " + e.getMessage());
            return "vehicles";
        }
    }

    @GetMapping("/fipe/consulta")
    public String showConsultaForm() {
        return "fipe-check";
    }
    
    @GetMapping("/fipe/{codigoFipe}")
    @ResponseBody
    public ResponseEntity<Double> consultarPrecoJson(@PathVariable String codigoFipe) {
        try {
            codigoFipe = codigoFipe.replace("-", "").trim();

            List<FipeResponse> resultados = fipeApiService.consultarPreco(codigoFipe);

            if (resultados.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(resultados.get(0).getValorAsDouble());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/fipe/consulta")
    public String consultarPreco(@RequestParam("codigoFipe") String codigoFipe, Model model) {
        try {
            List<FipeResponse> resultados = fipeApiService.consultarPreco(codigoFipe);

            if (resultados.isEmpty()) {
                model.addAttribute("errorMessage", "Código FIPE não encontrado.");
                return "fipe-check";
            }

            model.addAttribute("resultados", resultados);
            return "fipe-check";
        } catch (FipeCodigoInvalidoException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "fipe-check";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", "Erro inesperado: " + ex.getMessage());
            return "fipe-check";
        }
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        return "create-vehicle";
    }

    @PostMapping("/create")
    public String createVehicle(@Valid @ModelAttribute("vehicle") Vehicle newVehicle,
                                BindingResult result,
                                @RequestParam("files") MultipartFile[] files,
                                Model model,
                                RedirectAttributes redirectAttributes) {
    	if (result.hasErrors()) {
    	    log.warn("❌ Erros de validação ao cadastrar veículo:");
    	    result.getAllErrors().forEach(err -> log.warn(" - " + err.toString()));
    	    return "create-vehicle";
    	}

        if (result.hasErrors()) {
            return "create-vehicle";
        }

        try {
            Optional<User> userOptional = userService.getUsuarioLogado();
            if (userOptional.isEmpty()) {
                model.addAttribute("errorMessage", "Usuário não encontrado.");
                return "create-vehicle";
            }

            if (newVehicle.getFipeCode() != null && !newVehicle.getFipeCode().isBlank()) {
                try {
                    List<FipeResponse> resultados = fipeApiService.consultarPreco(newVehicle.getFipeCode());
                    if (!resultados.isEmpty()) {
                        newVehicle.setFipeTable(resultados.get(0).getValorAsDouble());
                    }
                } catch (Exception e) {
                    log.warn("Não foi possível buscar FIPE para código {}: {}", newVehicle.getFipeCode(), e.getMessage());
                }
            }

            newVehicle.setUser(userOptional.get());

            Vehicle savedVehicle = vehicleService.createVehicle(newVehicle);

            Path uploadPath = Paths.get("./uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Cria a associação de imagem com o veículo
                    VehicleImage img = new VehicleImage(fileName, savedVehicle);
                    savedVehicle.getImages().add(img);  // Isso já vai associar o veículo à imagem
                }
            }

            vehicleService.updateVehicle(savedVehicle);
            redirectAttributes.addFlashAttribute("successMessage", "Veículo cadastrado com sucesso!");
            return "redirect:/vehicles";

        } catch (br.com.AutoStock.exception.DuplicateVehicleException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "create-vehicle";

        } catch (Exception e) {
            log.error("Erro ao salvar veículo: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Erro ao salvar veículo: " + e.getMessage());
            return "create-vehicle";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUsuarioLogado();
        Optional<Vehicle> vehicleOptional = vehicleService.findById(id);

        if (userOptional.isEmpty() || vehicleOptional.isEmpty() ||
            !vehicleService.isVeiculoDoUsuario(vehicleOptional.get(), userOptional.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para editar este veículo.");
            return "redirect:/vehicles";
        }

        model.addAttribute("vehicle", vehicleOptional.get());
        return "update-vehicles";
    }

    @PostMapping("/update/{id}")
    public String updateVehicle(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("vehicle") Vehicle updatedVehicle,
                                BindingResult result,
                                @RequestParam(value = "files", required = false) MultipartFile[] files,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userService.getUsuarioLogado();
        Optional<Vehicle> vehicleOptional = vehicleService.findById(id);

        if (userOptional.isEmpty() || vehicleOptional.isEmpty()
                || !vehicleService.isVeiculoDoUsuario(vehicleOptional.get(), userOptional.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para editar este veículo.");
            return "redirect:/vehicles";
        }

        if (result.hasErrors()) {
            updatedVehicle.setId(id);
            updatedVehicle.setImages(vehicleOptional.get().getImages());
            model.addAttribute("vehicle", updatedVehicle);
            return "update-vehicles";
        }

        try {
            Vehicle vehicle = vehicleOptional.get();

            vehicle.setBrand(updatedVehicle.getBrand());
            vehicle.setModel(updatedVehicle.getModel());
            vehicle.setVersion(updatedVehicle.getVersion());
            vehicle.setPlate(updatedVehicle.getPlate());
            vehicle.setColor(updatedVehicle.getColor());
            vehicle.setRenavam(updatedVehicle.getRenavam());
            vehicle.setChassis(updatedVehicle.getChassis());
            vehicle.setMileage(updatedVehicle.getMileage());
            vehicle.setPurchasePrice(updatedVehicle.getPurchasePrice());
            vehicle.setFipeCode(updatedVehicle.getFipeCode());
            vehicle.setExpenses(updatedVehicle.getExpenses());
            vehicle.setManufactureYear(updatedVehicle.getManufactureYear());
            vehicle.setModelYear(updatedVehicle.getModelYear());
            vehicle.setRiskCategory(updatedVehicle.getRiskCategory());
            vehicle.setFuel(updatedVehicle.getFuel());
            vehicle.setTransmission(updatedVehicle.getTransmission());
            vehicle.setDoors(updatedVehicle.getDoors());
            vehicle.setAdditionalInfo(updatedVehicle.getAdditionalInfo());

            if (updatedVehicle.getFipeCode() != null && !updatedVehicle.getFipeCode().isBlank()) {
                try {
                    List<FipeResponse> resultados = fipeApiService.consultarPreco(updatedVehicle.getFipeCode());
                    if (!resultados.isEmpty()) {
                        vehicle.setFipeTable(resultados.get(0).getValorAsDouble());
                    }
                } catch (Exception e) {
                    log.warn("Não foi possível atualizar FIPE para código {}: {}", updatedVehicle.getFipeCode(), e.getMessage());
                }
            }

            if (files != null && files.length > 0) {
                Path uploadPath = Paths.get("./uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        VehicleImage img = new VehicleImage(fileName, vehicle);
                        vehicle.getImages().add(img);
                    }
                }
            }

            vehicleService.updateVehicle(vehicle);

            redirectAttributes.addFlashAttribute("successMessageUp", "Veículo atualizado com sucesso!");
            return "redirect:/vehicles";

        } catch (br.com.AutoStock.exception.DuplicateVehicleException ex) {
            updatedVehicle.setId(id);
            updatedVehicle.setImages(vehicleOptional.get().getImages());
            model.addAttribute("vehicle", updatedVehicle);
            model.addAttribute("errorMessage", ex.getMessage());
            return "update-vehicles";

        } catch (Exception e) {
            log.error("Erro ao atualizar veículo ID {}: {}", id, e.getMessage(), e);
            updatedVehicle.setId(id);
            updatedVehicle.setImages(vehicleOptional.get().getImages());
            model.addAttribute("vehicle", updatedVehicle);
            model.addAttribute("errorMessage", "Erro ao atualizar veículo: " + e.getMessage());
            return "update-vehicles";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUsuarioLogado();
        Optional<Vehicle> vehicleOptional = vehicleService.findById(id);

        if (userOptional.isEmpty() || vehicleOptional.isEmpty() ||
            !vehicleService.isVeiculoDoUsuario(vehicleOptional.get(), userOptional.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para excluir este veículo.");
            return "redirect:/vehicles";
        }

        vehicleService.deleteVehicle(id);
        redirectAttributes.addFlashAttribute("successMessageDel", "Veículo excluído com sucesso!");
        return "redirect:/vehicles";
    }
    
    @PostMapping("/delete-image/{imageId}")
    public String deleteImage(@PathVariable("imageId") Long imageId,
                              @RequestParam("vehicleId") Long vehicleId,
                              RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteImage(imageId);
            redirectAttributes.addFlashAttribute("successMessageUp", "Imagem excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir imagem: " + e.getMessage());
        }
        return "redirect:/vehicles/edit/" + vehicleId;
    }

}