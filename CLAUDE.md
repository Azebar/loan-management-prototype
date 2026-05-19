# CLAUDE.md

Operational notes for future Claude sessions in this repo. The user-facing
explanation of the project is in `README.md`; this file is the
codebase contract — what to follow, what not to change.

## Stack at a glance

- **Backend** — Java 25 (Temurin), Spring Boot 3.5, Gradle 9 (Kotlin DSL),
  Flyway, Spring Data JPA, OpenCSV, springdoc-openapi.
- **Frontend** — React 19, TypeScript 5.7, Vite 6, plain CSS (dark theme),
  no UI library.
- **DB** — PostgreSQL 17.
- **Ops** — Multi-stage Dockerfiles, `docker-compose.yml` with healthchecks.

## Architecture rules — do not violate

The backend is **onion / hexagonal**. Dependencies point inward only.

```
presentation  →  application  →  domain  ←  infrastructure (implements port)
```

Layer responsibilities (`backend/src/main/java/com/lhv/loans/`):

| Package | What lives here | What MUST NOT live here |
| --- | --- | --- |
| `domain.model` | Pure Java records (`Loan`, `RepaymentSchedule`, enums) | `@Entity`, `@Component`, Jackson annotations, `Instant.now()` |
| `domain.service` | Business logic: calculators, dispatcher | Persistence concerns, Spring web types |
| `domain.port` | Outbound interfaces (`LoanRepository`) | Spring Data types |
| `application` | Use-case orchestration, commands, transactions | HTTP/JSON concerns, business rules |
| `infrastructure.persistence` | JPA entities, adapters | Domain logic |
| `presentation.rest` | Controllers, DTOs, exception handler | Business rules, JPA |

**If a new feature blurs these boundaries, stop and refactor instead of merging
across layers.** That's the whole point of the structure.

## Coding conventions

### Money
- Always `BigDecimal`, never `double` or `float`.
- Money values are scale 2 with `RoundingMode.HALF_UP` — go through
  `MoneyMath.money(...)`.
- Intermediate rate math runs at `MathContext.DECIMAL64`
  (`MoneyMath.MC`); only round at the end.
- Annual rate is stored as a **percent** (e.g. `5.25` = 5.25%), not a fraction.

### Schedule calculators (`domain.service`)
- New repayment models are added by creating a new `@Component implements
  RepaymentScheduleCalculator` and declaring its `supports()` enum value.
- **Do not** modify `RepaymentScheduleService` to add a `switch` or `if` chain
  — it auto-discovers calculators by their `supports()` value.
- Invariants every calculator MUST uphold (cover in tests):
  - `totalPrincipal == loan.amount()` (full amortization)
  - `totalPaid == totalPrincipal + totalInterest`
  - Last installment's `remainingBalance` is `0.00`
  - The final installment absorbs rounding residue.

### Time
- Inject `Clock` (see `ClockConfig`). **Never** call `Instant.now()`,
  `LocalDate.now()`, `System.currentTimeMillis()` from production code — it
  defeats deterministic testing.

### Validation
- Validate at the edge only: `@Valid` on request DTOs + invariants in record
  compact constructors. Application & domain trust their callers.
- `LoanNotFoundException` and `IllegalArgumentException` are translated to
  404 / 400 by `GlobalExceptionHandler` — don't catch and re-throw them
  yourself.

### DB
- Schema changes = a new file under `backend/src/main/resources/db/migration/`
  named `V{N}__description.sql`. **Never** edit an existing migration that's
  been applied anywhere — that wedges Flyway. `spring.jpa.hibernate.ddl-auto`
  is `validate` for the same reason.

### Frontend
- The Vite dev server proxies `/api/*` → `localhost:8080`. In Docker, nginx
  does the same proxy to `backend:8080`. The app never hardcodes a backend
  URL.
- `tsconfig.json` has `noEmit: true` so `tsc -b` only typechecks; Vite does
  the actual transpile. Don't reintroduce emit.

## Common commands

```bash
# Backend
cd backend
./gradlew test                # unit tests (no DB needed)
./gradlew bootRun             # needs Postgres up; see docker-compose service
./gradlew build               # full build + tests

# Frontend
cd frontend
npm install
npm run dev                   # http://localhost:5173, proxies /api → :8080
npm run build                 # production build → dist/

# Full stack
docker compose up --build     # postgres + backend + frontend
docker compose down -v        # tear down INCLUDING the postgres volume
```

## What's done

- [x] Project scaffold + monorepo layout
- [x] Backend (onion architecture) with annuity / equal-principal / bullet
      calculators, REST + CSV export, Swagger UI
- [x] Unit tests for all three calculators (textbook values + invariants),
      9 tests pass
- [x] React + TS frontend: create / edit / delete / list loans, schedule view,
      CSV download link
- [x] Docker compose with healthchecks, multi-stage builds
- [x] README with architecture rationale and run instructions
- [x] Local git init, two commits authored as
      `Artur Krivorokov <artur.krivorokov@gmail.com>` (personal identity set
      via repo-local `git config`, the global work identity is untouched)
- [x] End-to-end smoke test against a real Postgres (a 240-mo €150k annuity
      @ 4.25% gives €928.85/mo and amortizes to 0.00)
- [x] Pushed to GitHub at https://github.com/Azebar/loan-management-prototype
      via HTTPS + a classic PAT (the `osxkeychain` helper is configured
      globally but its binary is missing under Homebrew git, so credentials
      aren't cached on this machine; that's a known wart, not a regression).

## What's left

The brief is fully delivered. Anything below is a nice-to-have, not required.

### Nice-to-haves
- PDF export of the schedule (would require adding e.g. OpenPDF; CSV already
  satisfies the brief's "export" optional requirement).
- Integration tests with Testcontainers Postgres for the persistence adapter
  and full REST round-trip.
- Variable / step-rate calculator — drops in as a fourth
  `RepaymentScheduleCalculator` implementation, no other code needs to
  change.
- Auth / multi-tenancy — explicitly out of scope per the brief (internal
  tool).

## Git / commit hygiene

- The repo's **local** `user.email` is the personal Gmail; the global config
  is the user's work email. **Never** unset the local override.
- `tsconfig.tsbuildinfo` and the stray `.js` from `tsc` emit are
  gitignored — if they reappear, something has regressed in the frontend
  tsconfig.
- Commit messages: short imperative subject, blank line, prose body. See
  existing commits for tone.
