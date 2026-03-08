package br.com.AutoStock.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NaturalId(mutable = true)
    @Email(message = "E-mail inválido.")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "enabled")
    private boolean enabled = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "first_login_completed")
    private boolean firstLoginCompleted = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles = new ArrayList<>();

    @Column(name = "failed_attempts")
    private int failedAttempts;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    // === Empresa ===
    @Column(length = 14, unique = true, nullable = false)
    @CNPJ(message = "CNPJ inválido.")
    private String cnpj;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(name = "inscricao_estadual")
    private String inscricaoEstadual;

    // === Endereço ===
    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    // === Conta de acesso ===
    @Column(nullable = false)
    private String telefone;

    // === Relacionamentos ===
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PasswordHistory> passwordHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    // === Construtores ===
    public User(String email, String password, String cnpj, String razaoSocial,
                String nomeFantasia, String inscricaoEstadual,
                String cep, String logradouro, String numero, String complemento,
                String bairro, String cidade, String uf, String telefone,
                Collection<Role> roles) {
        this.email = email;
        this.password = password;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.inscricaoEstadual = inscricaoEstadual;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.telefone = telefone;
        this.roles = roles;

        this.enabled = false;
        this.firstLoginCompleted = false;
        this.failedAttempts = 0;
        this.accountLocked = false;
    }

	public void incrementFailedAttempts() {
		this.failedAttempts++;
	}

	public void resetFailedAttempts() {
		this.failedAttempts = 0;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public boolean isLockTimeExpired() {
		return lockTime != null && lockTime.isBefore(LocalDateTime.now().minusMinutes(5));
	}
}
