# 🛡️ CIBERGU 2026 - DreamTeam API

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://jdk.java.net/25/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Security-Spring_Security_6.4-blue.svg)](https://spring.io/projects/spring-security)
[![Hackathon](https://img.shields.io/badge/Event-CIBERGU_2026-red.svg)](https://ceei-gu.es/)

Sistema de autenticación y gestión de perfiles de alta seguridad desarrollado para el torneo de ciberseguridad **CIBERGU 2026** (Guadalajara, España). Este proyecto implementa una arquitectura **State-of-the-Art** basada en el principio de Privilegio Mínimo y Defensa Proactiva.

## 🚀 Arquitectura Técnica

El sistema se divide en tres capas críticas de seguridad, asegurando que cada componente actúe como una barrera independiente:

1.  **Capa de Persistencia e Infraestructura (Rol 1):** Gestión de datos mediante JPA y SQLite, garantizando integridad referencial y almacenamiento seguro de identidades.
2.  **Núcleo de Seguridad JWT (Rol 2):** Implementación estricta de **Spring Security 6.4** con autenticación apátrida (Stateless) mediante tokens JWT (JJWT 0.12.5), cifrado de credenciales con BCrypt y gestión de contexto de seguridad.
3.  **Perímetro Defensivo y Endpoints (Rol 3):** Validación estricta de entrada, mitigación de ataques de fuerza bruta y normalización de errores para evitar la fuga de información.

## 🛡️ Características de Seguridad (OWASP Mitigation)

Hemos diseñado esta API para repeler los ataques más comunes identificados por el jurado:

* **Prevención de Fuerza Bruta:** Implementación del algoritmo *Token Bucket* mediante **Bucket4j**. Límite estricto de **5 intentos por minuto por IP** en el endpoint de login.
* **Mitigación de Enumeración de Usuarios:** El sistema responde con mensajes de error genéricos (`401 Unauthorized`) tanto si el usuario no existe como si la contraseña es incorrecta, cegando las herramientas de reconocimiento.
* **Anti-XSS y Sanitización:** Validación de entrada mediante DTOs e inmutabilidad con `Records` de Java 25. Reglas estrictas mediante `@Pattern` alfanumérico para prevenir inyecciones de script.
* **Seguridad de Memoria:** Uso de las últimas características de **Java 25** para optimizar el rendimiento y la seguridad del runtime.
* **Documentación de Auditoría:** Contrato de API expuesto y testeable mediante **Swagger-UI / OpenAPI 3**.

## 🛠️ Tecnologías Utilizadas

| Componente | Tecnología |
| :--- | :--- |
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.0.5 |
| **Seguridad** | Spring Security 6.4 + JJWT 0.12.5 |
| **Base de Datos** | SQLite (Persistencia local aislada) |
| **Defensa DoS** | Bucket4j |
| **Documentación** | SpringDoc OpenAPI |

## 📦 Estructura del Proyecto

```text
com.cibergu.api
├── controllers   <-- Perímetro de entrada y Rate Limiting
├── dtos          <-- Validación y sanitización de datos (Inbound)
├── exceptions    <-- Gestión de errores y anti-enumeración
├── models        <-- Entidades de persistencia JPA
├── repositories  <-- Abstracción de base de datos
└── security      <-- Filtros JWT y Configuración Core
