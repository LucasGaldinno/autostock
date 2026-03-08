package br.com.AutoStock.model;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================
    // Dados Pessoais
    // ========================
    @NotBlank(message = "Nome é obrigatório.")
    private String firstName;

    @NotBlank(message = "Sobrenome é obrigatório.")
    private String lastName;

    @NotBlank(message = "CPF é obrigatório.")
    @CPF(message = "CPF inválido.")
    @Column(unique = true)
    private String cpf;

    @NotBlank(message = "RG é obrigatório.")
    private String rg;

    // ========================
    // Contato / Acesso
    // ========================
    @Email(message = "E-mail inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Telefone é obrigatório.")
    private String phone;

    // ========================
    // Endereço
    // ========================
    @NotBlank(message = "CEP é obrigatório.")
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório.")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório.")
    private String numero;

    private String complemento;

    @NotBlank(message = "Bairro é obrigatório.")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "UF é obrigatório.")
    @Column(length = 2)
    private String uf;

    // ========================
    // Relacionamento com a agência
    // ========================
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id", nullable = false)
    private User agency;

    // ========================
    // Status
    // ========================
    private boolean active = true;
}
