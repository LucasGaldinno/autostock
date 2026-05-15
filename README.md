## 📌 Sobre o Repositório

Este repositório contém uma versão demonstrativa do projeto AutoStock, desenvolvida para fins acadêmicos e apresentação de portfólio.

Por questões de infraestrutura e configuração do ambiente acadêmico utilizado durante o desenvolvimento do projeto, alguns componentes não foram incluídos neste repositório, como:

* Arquivos de ambiente (.env)
* Scripts de deploy
* Configurações de infraestrutura
* Recursos utilizados no ambiente Azure

O objetivo deste repositório é apresentar:

* Arquitetura da aplicação
* Tecnologias utilizadas
* Estrutura do projeto
* Principais funcionalidades implementadas

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
Spring Security + Authentication<br>
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

### 📱 Compatibilidade Mobile

A aplicação foi desenvolvida com interface responsiva utilizando HTML, CSS e JavaScript, permitindo acesso ao sistema por dispositivos móveis diretamente pelo navegador.

O objetivo foi garantir uma experiência consistente entre desktop e smartphones, facilitando o acesso remoto à plataforma por gestores e colaboradores.

<div align="center">

Web Application (Spring Boot + Thymeleaf)

↓

Accessible via Browser

↓

Responsive Interface for Mobile Devices

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

## ☁ Deploy

☁ Deploy

Durante o desenvolvimento acadêmico do projeto, foram utilizados Docker e Microsoft Azure para configuração e deploy da aplicação no ambiente disponibilizado pela instituição de ensino.

A infraestrutura utilizada no projeto acadêmico não está mais disponível neste repositório.

---

## 🔒 Segurança

Para garantir a segurança da aplicação foram implementados:

Autenticação com Spring Security

Proteção de rotas e controle de permissões

---

## 🔐 Autenticação

O sistema utiliza autenticação baseada em **Spring Security e JWT**.

Durante o processo de cadastro, o usuário precisa **validar seu e-mail** antes de acessar a plataforma.

Para isso, é utilizado um serviço **SMTP**, responsável por enviar o e-mail de confirmação contendo o link de ativação da conta.

---

## 🎓 Projeto Acadêmico

Este projeto foi desenvolvido como **Trabalho de Conclusão de Curso (TCC)** do curso de **Sistemas de Informação**, com o objetivo de aplicar conceitos de:

- Engenharia de Software
- Desenvolvimento Web
- Segurança de Aplicações
- Arquitetura de Sistemas

---

## 🌐 Integrações Externas

O sistema realiza integração com APIs externas para automatizar validações e consultas de dados utilizados pela plataforma.

### BrasilAPI

Utilizada para:

* Consulta e validação de CEP
* Consulta de dados de empresas via CNPJ
* Consulta de valores da Tabela FIPE

Documentação oficial: [BrasilAPI Docs](https://brasilapi.com.br/docs?utm_source=chatgpt.com)

As integrações foram realizadas através de requisições HTTP utilizando APIs REST em formato JSON.

---

## 📸 Screenshots

### 🔐 Tela de Login

<div align="center">
  <img src="https://raw.githubusercontent.com/LucasGaldinno/autostock/refs/heads/main/docs/screenshots/Tela-acesso.png" alt="Tela de Login" width="800"/>
</div>

---

### 📊 Dashboard

<div align="center">
  <img src="https://raw.githubusercontent.com/LucasGaldinno/autostock/refs/heads/main/docs/screenshots/Tela-dashboard.png" alt="Dashboard do Sistema" width="800"/>
</div>

---

### 🚘 Gestão de Veículos

<div align="center">
  <img src="https://raw.githubusercontent.com/LucasGaldinno/autostock/refs/heads/main/docs/screenshots/Tela-estoque.png" alt="Gestão de Veículos" width="800"/>
</div>

---

### 💰 Gestão Financeira

<div align="center">
  <img src="./docs/financeiro.png" alt="Gestão Financeira" width="800"/>
</div>

---

### 📄 Contratos

<div align="center">
  <img src="https://raw.githubusercontent.com/LucasGaldinno/autostock/refs/heads/main/docs/screenshots/Tela-gerarContrato.png" alt="Contratos Digitais" width="800"/>
</div>

---

## 👨‍💻 Desenvolvedores

### Pedro Henrique Guedes

* Documentação
* Infraestrutura e deploy
* Docker
* Microsoft Azure

### Lucas Galdino

* Frontend (HTML, CSS, JavaScript, Bootstrap e Thymeleaf)
* Backend com Java e Spring Boot
* APIs REST
* Spring Security e JWT
* Modelagem e integração com MySQL
* JPA / Hibernate
* Documentação

