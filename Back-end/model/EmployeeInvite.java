package br.com.AutoStock.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employee_invites")
public class EmployeeInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email do funcionário que será convidado
    @Column(nullable = false)
    private String email;

    // Token único do convite
    @Column(nullable = false, unique = true, length = 100)
    private String token;

    // Agência que criou o convite
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private User agency;

    // Data de expiração (ex: 48h)
    private LocalDateTime expiresAt;

    // True depois que o funcionário usa o token
    private boolean used = false;
    
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
