# SIW Tornei — Progetto Docente

Sistema informativo per la gestione di tornei di calcio amatoriale per il corso di Sistemi Informativi su Web (a.a. 2025/2026, Roma Tre).

## Stack tecnologico

- **Spring Boot 3.3** (backend)
- **JPA / Hibernate** (persistenza)
- **PostgreSQL** (database relazionale)
- **Thymeleaf** (template engine server-side)
- **Spring Security** (autenticazione e autorizzazione)
- **React** via CDN (frontend per la classifica)
- **Java 17**, **Maven**

## Come avviare il progetto

## Struttura del progetto

```
ProgettoDocente/
├── pom.xml                          # Configurazione Maven (dipendenze)
├── README.md                        # Questo file
├── .gitignore                       # File da non versionare
└── src/main/
    ├── java/it/siw/tornei/
    │   ├── SiwTorneiApplication.java        # Classe main (avvia Spring Boot)
    │   ├── model/                           # Entita' JPA
    │   ├── repository/                      # Interfacce per il DB
    │   ├── service/                         # Logica di business + transazioni
    │   ├── controller/                      # Endpoint HTTP
    │   └── config/                          # Spring Security, Web, bootstrap utenti
    └── resources/
        ├── application.properties           # Configurazione runtime (DB, porta, ecc.)
        ├── templates/                       # Pagine Thymeleaf
        └── static/                          # CSS, JS, immagini statiche
```

## Architettura a livelli

Il progetto segue l'architettura a tre livelli richiesta dal corso:

- **Controller**: riceve la richiesta HTTP, valida i parametri, chiama il service, ritorna template o JSON.
- **Service**: contiene la logica di business, gestisce le transazioni con `@Transactional`.
- **Repository**: interfacce JPA che parlano col database. Niente logica.

I controller non chiamano mai i repository direttamente — sempre il service nel mezzo.
