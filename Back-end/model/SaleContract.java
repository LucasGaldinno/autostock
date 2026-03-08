package br.com.AutoStock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
    name = "sale_contracts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "vehicle_id") // garante 1 contrato por veículo
    }
)
public class SaleContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // ===== RELACIONAMENTOS =====
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ======= DADOS DO CLIENTE =======
    @Column(nullable = false, length = 150)
    private String customerName;

    @Column(length = 14)
    private String cpf;

    @Column(length = 20)
    private String customerPhone;

    @Column(length = 100)
    private String customerEmail;

    @Column(length = 120)
    private String address;

    @Column(length = 10)
    private String number;

    @Column(length = 50)
    private String complement;

    @Column(length = 60)
    private String neighborhood;

    @Column(length = 60)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(length = 9)
    private String cep;

    // ======= INFORMAÇÕES DE VENDA =======
    @Column(nullable = false)
    private Double salePrice;

    @Column(length = 30)
    private String negotiationType;

    // ======= METADADOS =======
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 255)
    private String pdfPath; // caminho no sistema (opcional, se for salvar arquivo localmente)

    // ======= MÉTODOS DE CONVENIÊNCIA =======
    public String getFormattedVehicleInfo() {
        if (vehicle == null) return "";
        return vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlate() + ")";
    }
}
