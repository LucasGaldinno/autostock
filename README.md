# 🚗 AutoStock

AutoStock é uma plataforma web e mobile desenvolvida para automatizar a gestão de pequenas e médias agências de veículos, substituindo processos manuais por uma solução digital integrada.

O sistema permite o gerenciamento de estoque de veículos, controle financeiro, emissão de contratos digitais, gestão de garantias e geração de relatórios, proporcionando mais eficiência operacional e organização para as agências.

---

## ⚙ Tech Stack

Java • Spring Boot • Spring Security • MySQL • Git

---

## 📌 Problema

Muitas agências de veículos de pequeno e médio porte ainda utilizam processos manuais para gerenciar suas operações, como:

- Controle de estoque
- Emissão de contratos
- Gestão financeira
- Controle de garantias
- Relatórios de vendas

Esses processos aumentam o risco de erros operacionais, dificultam a organização e reduzem a eficiência da gestão.

---

## 🎯 Objetivo

O objetivo do **AutoStock** é modernizar a gestão dessas agências por meio de uma aplicação digital que permita:

- Automatizar processos administrativos
- Melhorar o controle do estoque de veículos
- Facilitar a geração de contratos de venda
- Monitorar indicadores financeiros
- Integrar dados de mercado, como a **Tabela FIPE**

---

## 🧩 Funcionalidades

### 🚘 Gestão de Veículos

- Cadastro de veículos
- Controle de estoque
- Classificação por categoria

### 📄 Contratos

- Geração de contratos digitais de compra e venda
- Registro de histórico de vendas

### 💰 Gestão Financeira

- Controle de gastos por veículo
- Cálculo de margem de lucro
- Relatórios de vendas

### 🛡 Garantias

- Registro e controle de garantias de veículos vendidos

### 🔗 Integração de Dados

- Consulta de valores pela **Tabela FIPE**

### 👥 Gestão de Usuários

- Cadastro de funcionários
- Convite de novos colaboradores
- Controle de permissões

### 📊 Dashboard

- Indicadores de vendas
- Relatórios mensais
- Crescimento de vendas

---

## 🏗 Arquitetura do Sistema

A aplicação foi desenvolvida seguindo uma **arquitetura em camadas**, garantindo organização, escalabilidade e facilidade de manutenção.

<div align="center">

Frontend (HTML + Thymeleaf)<br>
↓<br>
Backend API (Spring Boot)<br>
↓<br>
Spring Security + JWT Authentication<br>
↓<br>
Service Layer (Regras de Negócio)<br>
↓<br>
Repository Layer (JPA / Hibernate)<br>
↓<br>
MySQL Database

</div>

---

## 💻 Tecnologias Utilizadas

### Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- REST API

### Frontend

- HTML5
- CSS3
- JavaScript
- Bootstrap
- Thymeleaf

### Banco de Dados

- MySQL
- MySQL Workbench

### Segurança

- Autenticação com **Spring Security**
- Autorização baseada em **JWT (JSON Web Token)**
- Proteção de rotas e controle de permissões

---

## 📱 Plataformas

### 🌐 Versão Web

Plataforma principal utilizada pelas agências para gerenciamento completo das operações, incluindo:

- Cadastro e gerenciamento de veículos
- Controle de estoque
- Gestão financeira
- Emissão de contratos digitais
- Controle de garantias
- Relatórios de vendas e desempenho
- Gerenciamento de usuários e permissões

### 📱 Versão Mobile

A versão mobile permite acesso ao sistema por meio de dispositivos móveis.

A aplicação utiliza **WebView**, permitindo que a interface web seja executada dentro de um aplicativo mobile, garantindo:

- Acesso remoto à plataforma
- Mobilidade para gestores
- Experiência consistente entre web e mobile

<div align="center">

Web Application (Spring Boot + Thymeleaf)

↓

Accessible via Browser

↓

Mobile App using WebView

</div>

---

## 📊 Benefícios da Solução

- Redução de erros operacionais
- Automatização de processos administrativos
- Melhor organização do estoque
- Maior controle financeiro
- Geração rápida de contratos
- Melhor tomada de decisão através de relatórios

---

## 🚀 Melhorias Futuras

Algumas melhorias planejadas para evolução do projeto:

- Containerização da aplicação com **Docker**
- Deploy em ambiente de **Cloud**
- Documentação da API com **Swagger / OpenAPI**
- Implementação de **testes automatizados**
- Aplicativo mobile nativo

---

## 🎓 Projeto Acadêmico

Este projeto foi desenvolvido como **Trabalho de Conclusão de Curso (TCC)** do curso de **Sistemas de Informação**, com o objetivo de aplicar conceitos de:

- Engenharia de Software
- Desenvolvimento Web
- Segurança de Aplicações
- Arquitetura de Sistemas

---
## 👨‍💻 Autor

**Pedro Henrique Guedes**

Documentação

## 👨‍💻 Autor

**Lucas Galdino**

Documentação</br>
Desenvolvedor Full Stack  
Java • Spring Boot • APIs • Backend Development
