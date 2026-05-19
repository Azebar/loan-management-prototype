# Loan Management Prototype

A small full-stack prototype that lets a product analyst create loans and inspect
the corresponding repayment schedules. Built as a take-home exercise to show how
I'd structure a small but real Java service end-to-end.

> **Stack** — Java 25 · Spring Boot 3.5 · PostgreSQL 17 · Flyway · React 19 · TypeScript · Vite · Docker Compose

---

## What the app does

* **CRUD on loans** with the fields required by the brief: loan type, amount,
  term in months, annual interest rate, repayment schedule type, plus a
  borrower name and start date.
* **Generates the repayment schedule** for three repayment models:
  * **Annuity** — equal periodic payment, classic mortgage formula
    `A = P · r / (1 − (1+r)⁻ⁿ)`.
  * **Equal principal** — fixed principal each period; interest (and thus
    total payment) decreases over time.
  * **Bullet** — interest-only payments, full principal due in the final
    period.
* **CSV export** of any generated schedule.
* **Swagger UI** at `/swagger-ui.html` for exploring the REST API.
* **React UI** for a non-developer to drive the whole flow without `curl`.

## Architecture — why it looks the way it does

The backend uses an **onion / hexagonal** layout. There are four concentric
layers and dependencies only point inward:

```
presentation (REST controllers, DTOs, exception handler)
    └── application (use-case services, commands, transactions)
            └── domain (model, business rules, ports)
                    ▲
                    │   implements
                    │
              infrastructure (JPA entities, Spring Data adapter)
```

Concretely:

| Layer | Package | Responsibility |
| --- | --- | --- |
| `domain.model` | `Loan`, `RepaymentSchedule`, enums | Pure value objects (Java records). No JPA, no Spring. |
| `domain.service` | `*ScheduleCalculator`, `RepaymentScheduleService` | Business rules: the actual maths. |
| `domain.port` | `LoanRepository` | Outbound port owned by the domain. |
| `application` | `LoanApplicationService`, command records | Orchestrates the use cases, owns transaction boundaries. |
| `infrastructure.persistence` | `LoanEntity`, `LoanRepositoryAdapter` | JPA implementation of the port. |
| `presentation.rest` | `LoanController`, request/response DTOs, exception handler | HTTP layer. |

The split keeps the schedule maths trivially unit-testable (no Spring context),
and lets the storage choice change without touching the domain.

### Design choices worth pointing out

* **Java records throughout** for the domain & DTOs — they're immutable,
  equals/hashCode are free, and they keep the brief code-light.
* **`BigDecimal`, never `double`, for money.** Money is rounded to two decimals
  with `HALF_UP`; intermediate rate maths runs at `MathContext.DECIMAL64`.
* **The final installment absorbs rounding remainders**, so every generated
  schedule fully amortizes (total principal === loan amount).
* **A `Map<ScheduleType, Calculator>` registry** is auto-wired by Spring from
  every `@Component` that implements `RepaymentScheduleCalculator`. Adding a new
  repayment model is one new class, no `if`/`switch`.
* **Flyway-managed schema** so the DB state is reproducible from a clean
  Postgres volume.
* **Validation at the edge only** — `@Valid` + Bean Validation on the request
  DTOs and invariants in the `Loan` record's compact constructor. The middle
  layers trust their inputs.
* **`Clock` is a bean** rather than `Instant.now()` peppered through the code,
  so `LoanApplicationService` is trivially testable with a fixed clock.

## Running it

> Requires Docker Desktop (or any Docker engine) and free ports 5173, 8080,
> 5432. Nothing else needs to be installed — Java, Node and Gradle are baked
> into the build stages.

```bash
git clone <this-repo>
cd loan-management-prototype
cp .env.example .env        # optional, just to customise ports/credentials
docker compose up --build
```

Then open:

* **Web UI** → http://localhost:5173
* **Swagger UI** → http://localhost:8080/swagger-ui.html
* **REST API** → http://localhost:8080/api/loans

The first `up --build` takes a few minutes (it downloads the Gradle, JDK and
Node base images and runs Gradle once to bootstrap the dependency cache).
Subsequent runs are seconds.

### Developing without Docker

```bash
# 1. start just Postgres
docker compose up -d postgres

# 2. backend
cd backend
./gradlew bootRun

# 3. frontend (in another shell)
cd frontend
npm install
npm run dev          # http://localhost:5173, proxies /api → :8080
```

### Tests

```bash
cd backend && ./gradlew test
```

Calculator tests cover textbook values, full-amortization invariants, the
zero-interest edge case, decreasing-interest invariant for equal-principal, and
the interest-only + balloon pattern for bullet loans.

## REST API at a glance

| Method | Path | Notes |
| --- | --- | --- |
| `GET`    | `/api/loans` | List all loans, newest first. |
| `POST`   | `/api/loans` | Create a loan. Validation errors return 400 with a per-field list. |
| `GET`    | `/api/loans/{id}` | Single loan. |
| `PUT`    | `/api/loans/{id}` | Replace all editable fields. |
| `DELETE` | `/api/loans/{id}` | Delete a loan. |
| `GET`    | `/api/loans/{id}/schedule` | JSON repayment schedule + totals. |
| `GET`    | `/api/loans/{id}/schedule.csv` | Same as a downloadable CSV. |

Example:

```bash
curl -X POST http://localhost:8080/api/loans \
  -H 'Content-Type: application/json' \
  -d '{
    "borrowerName": "Mari Maasikas",
    "type": "MORTGAGE",
    "amount": 150000.00,
    "termMonths": 240,
    "annualInterestRatePercent": 4.25,
    "scheduleType": "ANNUITY",
    "startDate": "2026-06-01"
  }'
```

## Scope notes (what I deliberately didn't build)

* **No auth.** The brief frames this as an internal tool; adding Spring Security
  would be cargo culting for a prototype.
* **No PDF export.** CSV is enough to demonstrate the export pattern; PDF would
  add a heavyweight dependency without changing the architecture.
* **No frontend tests.** The UI is intentionally thin glue; calculator
  correctness — where bugs would hurt — is covered by the backend unit tests.
* **One Flyway migration only.** Future schema changes are the natural next
  use of the existing migration setup.
* **No variable-rate / step-rate modelling.** Adding it is a new calculator
  implementation behind the existing `RepaymentScheduleCalculator` interface;
  the rest of the system doesn't need to know.

## Project layout

```
.
├── backend/                    Spring Boot service
│   ├── build.gradle.kts        Gradle Kotlin DSL
│   ├── Dockerfile              multi-stage JDK 25 build
│   └── src/main/java/com/lhv/loans/
│       ├── domain/             ← pure business logic
│       ├── application/        ← use-case orchestration
│       ├── infrastructure/     ← JPA adapter
│       └── presentation/       ← REST controllers
├── frontend/                   Vite + React 19 + TS
│   ├── Dockerfile              multi-stage build + nginx
│   ├── nginx.conf              SPA fallback + /api → backend proxy
│   └── src/
├── docker-compose.yml          Postgres + backend + frontend
├── .env.example                ports / credentials
└── README.md                   you are here
```
