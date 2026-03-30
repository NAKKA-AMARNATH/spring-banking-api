# Banking System API

A secure REST API for a banking system built with Spring Boot, featuring JWT authentication, role-based access control, account management, fund transfers, and loan processing.

## Tech Stack

- Java 17 + Spring Boot 4
- Spring Security 6 + JWT (jjwt)
- Spring Data JPA + Hibernate
- PostgreSQL
- Lombok
- Maven

## Features

- JWT authentication with access & refresh tokens
- Role-based access control: `CUSTOMER`, `MANAGER`, `ADMIN`
- Account management (Savings, Current, Fixed Deposit)
- Deposits, withdrawals, and fund transfers
- Loan application and approval workflow
- Audit logging for all critical actions
- Global exception handling with consistent API responses

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### Setup

1. Create the database:
```sql
CREATE DATABASE bankdb;
```

2. Update credentials in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankdb
    username: your_username
    password: your_password
```

3. Run the application:
```bash
mvn spring-boot:run
```

Hibernate will auto-create all tables on first startup.

## API Reference

Base URL: `http://localhost:8080`

All protected endpoints require the header:
```
Authorization: Bearer <access_token>
```

All responses follow the format:
```json
{
  "success": true,
  "message": "...",
  "data": {},
  "timestamp": "2026-03-30T..."
}
```

### Auth (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive tokens |
| POST | `/api/auth/refresh` | Refresh access token (`X-Refresh-Token` header) |

**Register:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "+1234567890",
  "nationalId": "NID123456"
}
```

**Login:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Accounts

| Method | Endpoint | Description |
|--------|----------|-------------|


| POST | `/api/accounts` | Create a new account |
| GET | `/api/accounts` | Get all my accounts |
| GET | `/api/accounts/{id}` | Get account by ID |

**Create Account:**
```json
{
  "accountType": "SAVINGS",
  "currency": "INR"
}
```
> Account types: `SAVINGS`, `CURRENT`, `FIXED_DEPOSIT`

### Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions/deposit/{accountId}` | Deposit funds |
| POST | `/api/transactions/withdraw/{accountId}` | Withdraw funds |
| POST | `/api/transactions/transfer` | Transfer between accounts |
| GET | `/api/transactions/{id}` | Get transaction by ID |
| GET | `/api/transactions/account/{accountId}` | Get account transaction history (paginated) |

**Deposit / Withdraw:**
```json
{
  "amount": 5000.00,
  "description": "Optional note"
}
```

**Transfer:**
```json
{
  "fromAccountNumber": "ACCXXXXXXXXXXXXX",
  "toAccountNumber": "ACCYYYYYYYYYYYYY",
  "amount": 1000.00,
  "description": "Optional note"
}
```

**Transaction history supports pagination:**
```
GET /api/transactions/account/1?page=0&size=10
```

### Loans

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/loans` | Apply for a loan |
| GET | `/api/loans` | Get my loans |
| GET | `/api/loans/{id}` | Get loan by ID |

**Apply for Loan:**
```json
{
  "accountId": 1,
  "principalAmount": 50000.00,
  "tenureMonths": 24
}
```

### Manager Endpoints (MANAGER or ADMIN role)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/manager/loans/pending` | View all pending loan applications |
| PUT | `/api/manager/loans/{id}/approve?interestRate=10.5` | Approve a loan |
| PUT | `/api/manager/loans/{id}/reject` | Reject a loan |
| GET | `/api/manager/accounts` | View all accounts |
| PUT | `/api/manager/accounts/{id}/freeze` | Freeze an account |
| PUT | `/api/manager/accounts/{id}/unfreeze` | Unfreeze an account |

> To assign MANAGER role, update directly in the database:
> ```sql
> UPDATE users SET role = 'MANAGER' WHERE email = 'manager@example.com';
> ```

## Project Structure

```
src/main/java/com/amarnath/BankingSystem/Bank/
├── config/         # SecurityConfig
├── controller/     # REST controllers
├── service/        # Business logic
├── repository/     # JPA repositories
├── entity/         # JPA entities
├── dto/
│   ├── request/    # Request DTOs
│   └── response/   # Response DTOs
├── mapper/         # Entity ↔ DTO mappers
├── exception/      # Custom exceptions & GlobalExceptionHandler
├── security/       # JWT filter, token provider, UserDetailsService
└── enums/          # Role, AccountStatus, TransactionType, etc.
```

## Build

```bash
# Run
mvn spring-boot:run

# Build JAR
mvn clean package

# Run tests
mvn test
```
