# SIW Tornei — Progetto Docente

Sistema informativo per la gestione di tornei di calcio amatoriale.
Implementa tutti i requisiti del PDF "siw-progetto-2026-giugno-luglio".

## Stack
- **Backend**: Spring Boot 3.3, Spring Data JPA, Spring Security, Spring Validation
- **DB**: PostgreSQL
- **Frontend**: Thymeleaf + 1 modulo React (classifica) via CDN inline

## Setup iniziale

### 1. PostgreSQL
Installa PostgreSQL (https://www.postgresql.org/download/). Poi crea il DB:
```sql
CREATE DATABASE siw_tornei;
```
Apri `src/main/resources/application.properties` e modifica `username`/`password` se diversi dai tuoi (default: `postgres`/`postgres`).

### 2. Java 17 + Maven
Servono Java 17 e Maven 3.8+. Controlla con:
```bash
java -version
mvn -version
```

### 3. Avvio
Dalla cartella del progetto:
```bash
mvn spring-boot:run
```
Vai su http://localhost:8080.

### Utenti pre-configurati
Al primo avvio vengono creati questi utenti di test:
- **admin** / **admin123** (ruolo ADMIN)
- **user** / **user123** (ruolo USER)

Il database parte vuoto per il resto: crea tornei, squadre, giocatori, ecc. dall'interfaccia admin.

## Architettura

```
src/main/java/it/siw/tornei/
├── SiwTorneiApplication.java       # main
├── model/                          # entità JPA
├── repository/                     # interfacce JpaRepository
├── service/                        # logica di business, @Transactional
├── controller/                     # MVC e REST
├── config/                         # security, CORS, bootstrap utenti
└── dto/                            # DTO (es. RigaClassificaDTO)
```

## Casi d'uso coperti

### Pubblici
- Elenco tornei `/tornei`
- Dettaglio torneo `/tornei/{id}`
- Squadre partecipanti (nella dettaglio torneo)
- Calendario partite (nella dettaglio torneo)
- Dettaglio squadra con giocatori `/squadre/{id}`
- Classifica torneo `/tornei/{id}/classifica` (React)

### Utenti registrati
- Visualizzazione commenti (`partite/{id}`)
- Inserimento / modifica / eliminazione propri commenti

### Admin
- CRUD tornei
- CRUD squadre
- CRUD giocatori
- CRUD partite + registrazione risultato
- CRUD arbitri
- Eliminazione squadre/partite

## Analisi sperimentale N+1 (Sezione 8.2 PDF)

Endpoint riservato all'admin:
```
GET /admin/perf/fetch-strategies/{torneoId}
```
Confronta tre strategie di fetch sulla query "partite di un torneo":
- **LAZY** (default) — provoca N+1 query
- **JOIN FETCH** — 1 sola query
- **EntityGraph** — equivalente, ma dichiarativo

Restituisce un JSON con i tempi misurati. Prima di chiamarlo, popola un torneo con almeno 5-6 squadre e ~20 partite PLAYED per vedere la differenza.

## Endpoint REST per React
```
GET /rest/tornei/{id}/classifica  → JSON con la classifica calcolata
```
La pagina `tornei/{id}/classifica` monta un componente React (caricato via CDN) che chiama questo endpoint, mostra una tabella, ti permette di filtrare per nome squadra e cambiare ordinamento.

## Swagger
http://localhost:8080/swagger-ui.html

## Note tecniche
- Password: hash BCrypt
- CSRF abilitato per i form, disabilitato per `/rest/**`
- `ddl-auto=update` durante lo sviluppo. Per la consegna ufficiale considera `validate`.
- Log SQL attivo per debug (commenta `spring.jpa.show-sql` in produzione)
