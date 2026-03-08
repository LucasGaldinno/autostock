# Tech Stack

Java • Spring Boot • Spring Security • MySQL • Docker • Git

# 🚗 AutoStock

AutoStock é uma plataforma web e mobile desenvolvida para automatizar a gestão de pequenas e médias agências de veículos, substituindo processos manuais por uma solução digital integrada.

O sistema permite o gerenciamento de estoque de veículos, controle financeiro, emissão de contratos digitais, gestão de garantias e geração de relatórios, proporcionando mais eficiência operacional e organização para as agências.

---

# 📌 Problema

Muitas agências de veículos de pequeno e médio porte ainda utilizam processos manuais para:

- Controle de estoque
- Emissão de contratos
- Gestão financeira
- Controle de garantias
- Relatórios de vendas

Esses processos aumentam o risco de erros operacionais, dificultam a organização e reduzem a eficiência da gestão.

---

# 🎯 Objetivo

O objetivo do AutoStock é modernizar a gestão dessas agências através de uma aplicação digital que permita:

- Automatizar processos administrativos
- Melhorar o controle do estoque de veículos
- Facilitar a geração de contratos de venda
- Monitorar indicadores financeiros
- Integrar dados de mercado como a Tabela FIPE

---

# 🧩 Funcionalidades

## Gestão de Veículos
- Cadastro de veículos
- Controle de estoque
- Classificação por categoria

## Contratos
- Geração de contratos digitais de compra e venda
- Registro de histórico de vendas

## Gestão Financeira
- Controle de gastos por veículo
- Cálculo de margem de lucro
- Relatórios de vendas

## Garantias
- Registro e controle de garantias de veículos vendidos

## Integração de Dados
- Consulta de valores pela **Tabela FIPE**

## Gestão de Usuários
- Cadastro de funcionários
- Convite de novos colaboradores
- Controle de permissões

## Dashboard
- Indicadores de vendas
- Relatórios mensais
- Crescimento de vendas

---

# 🏗 Arquitetura do Sistema

A aplicação foi desenvolvida seguindo uma arquitetura em camadas para garantir organização, escalabilidade e manutenção do código.

<div align="center">

Frontend (HTML / Thymeleaf)<br>
↓<br>
Backend API (Spring Boot)<br>
↓<br>
Spring Security + JWT Authentication<br>
↓<br>
Service Layer (Regras de negócio)<br>
↓<br>
Repository Layer (JPA / Hibernate)<br>
↓<br>
MySQL Database

</div>

---

# 💻 Tecnologias Utilizadas

## Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- API REST

## Frontend

- HTML5
- CSS3
- JavaScript
- Bootstrap
- Thymeleaf

## Banco de Dados

- MySQL
- MySQL Workbench

## Segurança

- OAuth 2.0
- JWT (JSON Web Token)

## Infraestrutura

- Docker
- Microsoft Azure

---

# 📱 Plataformas

## Versão Web

Plataforma principal utilizada pelas agências para gerenciamento completo das operações, incluindo:

- Cadastro e gerenciamento de veículos
- Controle de estoque
- Gestão financeira
- Emissão de contratos digitais
- Controle de garantias
- Relatórios de vendas e desempenho
- Gerenciamento de usuários e permissões

## Versão Mobile

A versão mobile permite acesso completo ao sistema por meio de dispositivos móveis, oferecendo as mesmas funcionalidades da plataforma web.

A aplicação utiliza **WebView**, possibilitando que a interface web seja executada dentro de um aplicativo mobile, garantindo:

- Acesso remoto à plataforma
- Mobilidade para gestores
- Experiência consistente entre web e mobile

---

<div align="center">

Web Application (Spring Boot + Thymeleaf)

⬇️

Accessible via Browser

⬇️

Mobile App using WebView

</div>

---

# ☁ Deploy

A aplicação foi containerizada utilizando **Docker** e hospedada na **Microsoft Azure**, garantindo:

- Portabilidade
- Escalabilidade
- Facilidade de implantação
- Padronização de ambiente

---

# 🔒 Segurança

Para garantir a segurança da aplicação foram implementados:

- Autenticação com **OAuth2**
- Controle de sessão utilizando **JWT**
- Proteção de rotas com **Spring Security**

---

# 📊 Benefícios da Solução

- Redução de erros operacionais
- Automatização de processos administrativos
- Melhor organização do estoque
- Maior controle financeiro
- Geração rápida de contratos
- Melhoria na tomada de decisão através de relatórios

---

# 🎓 Projeto Acadêmico

Este projeto foi desenvolvido como trabalho de conclusão do curso de **Sistemas de Informação**, com o objetivo de aplicar conceitos de engenharia de software, desenvolvimento web, segurança e arquitetura de sistemas.

---

# 👨‍💻 Autor

Desenvolvido por:

**Lucas Galdino Sertão**

Desenvolvedor Full Stack  
Java | Spring Boot | APIs | Cloud

