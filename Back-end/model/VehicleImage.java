package br.com.AutoStock.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VehicleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public VehicleImage(String imageUrl, Vehicle vehicle) {
        this.imageUrl = imageUrl;
        this.vehicle = vehicle;
    }
    
    @Override
    public String toString() {
        return imageUrl;
    }
}
