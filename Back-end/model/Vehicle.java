package br.com.AutoStock.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.AutoStock.enums.ColorEnum;
import br.com.AutoStock.enums.FuelType;
import br.com.AutoStock.enums.TransmissionType;
import br.com.AutoStock.validation.LicensePlate;
import br.com.AutoStock.validation.Renavam;
import br.com.AutoStock.validation.ValidMileage;
import br.com.AutoStock.validation.Vin;
import br.com.AutoStock.validation.YearRange;
import br.com.AutoStock.validation.VehicleYear;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ValidMileage
@YearRange
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "A marca é obrigatória.")
    private String brand;          // Marca
    @NotBlank(message = "O modelo é obrigatório.")
    private String model;          // Modelo
    @Size(max = 50, message = "A versão deve ter no máximo 50 caracteres.")
    private String version;		   // Versão
    @LicensePlate
    private String plate;          // Placa
    @Enumerated(EnumType.STRING)
    private ColorEnum color;
    @Renavam
    private String renavam;        // Renavam
    @Vin
    private String chassis;        // Chassis
    @Min(value = 0, message = "A quilometragem não pode ser negativa")
    private Long mileage;       // Quilometragem

    @DecimalMin(value = "0.0", inclusive = true, message = "O preço de compra não pode ser negativo")
    private Double purchasePrice;  // Preço de compra

    @Column(name = "fipe_code")
    private String fipeCode;       // Código FIPE
    private Double fipeTable;      // Valor FIPE (preenchido via API)
    @DecimalMin(value = "0.0", inclusive = true, message = "As despesas não podem ser negativas")
    private Double expenses;       // Despesas

    @VehicleYear
    private Integer manufactureYear;  // Ano de fabricação

    @VehicleYear
    private Integer modelYear;        // Ano do modelo

    private String riskCategory;   // Baixo, Médio, Alto

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuel;         // Combustível (Gasolina, Etanol, Flex...)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmission; // Câmbio (Manual, Automático, CVT...)

    @Min(2)
    @Max(5)
    @Column(nullable = false)
    private Integer doors;         // Número de portas

    @Lob
    private String additionalInfo; // Informações adicionais
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("createdAt ASC")
    private List<VehicleImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean available = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}