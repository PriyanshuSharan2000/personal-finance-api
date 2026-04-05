# Personal Finance Manager API

A production-grade RESTful backend built with **Java 17**, **Spring Boot 3**, **Spring Security (JWT)**, **PostgreSQL**, and **Docker**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security 6 + JWT (JJWT 0.11) |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA + Hibernate |
| Validation | Jakarta Bean Validation |
| Docs | Swagger / OpenAPI 3 (SpringDoc) |
| Testing | JUnit 5, Mockito, MockMvc |
| Build | Maven |
| DevOps | Docker, Docker Compose |

---

## Features

- JWT-based authentication (register / login)
- Multi-user support — each user sees only their own data
- Transaction management (INCOME / EXPENSE) with category tagging and date filtering
- Monthly budget limits per category
- Monthly financial reports — income vs expense, net savings, category breakdown
- Budget status endpoint — shows % used vs limit for current month
- Scheduled daily budget alert check (logs warnings at 80% and 100% usage)
- Global exception handling with consistent API response format
- Swagger UI for live API testing

---

## Project Structure

```
src/main/java/com/financeapi/
├── config/          # SecurityConfig, SwaggerConfig
├── controller/      # AuthController, TransactionController, BudgetController, ReportController
├── service/         # AuthService, TransactionService, BudgetService, ReportService
├── repository/      # UserRepository, TransactionRepository, BudgetRepository
├── model/           # User, Transaction, Budget
├── dto/
│   ├── request/     # RegisterRequest, LoginRequest, TransactionRequest, BudgetRequest
│   └── response/    # AuthResponse, TransactionResponse, BudgetResponse, MonthlyReportResponse, ApiResponse
├── exception/       # GlobalExceptionHandler, ResourceNotFoundException, DuplicateResourceException
├── security/        # JwtUtil, JwtFilter, UserDetailsServiceImpl
└── scheduler/       # BudgetAlertScheduler
```

---

## Getting Started

### Option 1 — Docker Compose (recommended)

```bash
# Clone the repo
git clone https://github.com/PriyanshuSharan2000/personal-finance-api.git
cd personal-finance-api

# Start PostgreSQL + App
docker-compose up --build
```

App runs at: `http://localhost:8080`

### Option 2 — Run locally

**Prerequisites:** Java 17, Maven, PostgreSQL running locally

```bash
# Update src/main/resources/application.yml with your DB credentials

mvn clean install
mvn spring-boot:run
```

---

## API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT token |

### Transactions
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions` | Add transaction |
| GET | `/api/transactions` | Get all (filters: type, category, from, to) |
| GET | `/api/transactions/{id}` | Get by ID |
| PUT | `/api/transactions/{id}` | Update |
| DELETE | `/api/transactions/{id}` | Delete |

### Budgets
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/budgets` | Set monthly budget |
| GET | `/api/budgets?month=4&year=2026` | Get budgets for month |
| GET | `/api/budgets/{id}` | Get by ID |
| PUT | `/api/budgets/{id}` | Update |
| DELETE | `/api/budgets/{id}` | Delete |

### Reports
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/monthly?month=4&year=2026` | Monthly income/expense report |
| GET | `/api/reports/budget-status` | Current month budget usage |

---

## Sample API Usage

### 1. Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Priyanshu","email":"p@example.com","password":"secret123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"p@example.com","password":"secret123"}'
# Copy the token from response
```

### 3. Add a transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":850.00,"category":"FOOD","description":"Groceries","txnDate":"2026-04-05"}'
```

### 4. Get monthly report
```bash
curl http://localhost:8080/api/reports/monthly?month=4&year=2026 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## Swagger UI

Visit `http://localhost:8080/swagger-ui.html` after starting the app.
Click **Authorize**, paste your Bearer token, and test all endpoints interactively.

---

## Running Tests

```bash
mvn test
```

Tests use an in-memory H2 database (no PostgreSQL needed for tests).

---

## Transaction Categories

**Income:** `SALARY`, `FREELANCE`, `INVESTMENT`, `OTHER_INCOME`

**Expense:** `FOOD`, `RENT`, `TRANSPORT`, `ENTERTAINMENT`, `HEALTHCARE`, `SHOPPING`, `UTILITIES`, `EDUCATION`, `OTHER_EXPENSE`
