# SecureBank — Banking Management System

A full-stack banking web application built with **Java, Spring Boot, Spring MVC, Spring Data JPA, Hibernate, MySQL, and Thymeleaf**.

## Features

- User registration & secure login (Spring Security + BCrypt)
- Automatic bank account creation on registration (unique 10-digit account number)
- Deposit, Withdraw, Fund Transfer (atomic, `@Transactional`)
- Transaction history
- Responsive Bootstrap dashboard
- Layered architecture: `controller / service / repository / entity / dto / exception / config`
- Global exception handling with friendly error pages
- Server-side + client-side validation

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 (Spring MVC, Spring Data JPA, Spring Security) |
| ORM | Hibernate |
| Database | MySQL 8 |
| View | Thymeleaf + Bootstrap 5 |
| Build | Maven |

## Project Structure

```
banking-app/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/bank/
    │   │   ├── BankingApplication.java
    │   │   ├── controller/       # AuthController, DashboardController, AccountController, TransactionController
    │   │   ├── service/          # UserService, AccountService, TransactionService (interfaces)
    │   │   ├── service/impl/     # Implementations with business logic
    │   │   ├── repository/       # UserRepository, AccountRepository, TransactionRepository
    │   │   ├── entity/           # User, Account, Transaction + enums/
    │   │   ├── dto/               # RegisterDto, DepositDto, WithdrawDto, TransferDto, AccountSummaryDto
    │   │   ├── exception/        # Custom exceptions + GlobalExceptionHandler
    │   │   └── config/           # SecurityConfig, CustomUserDetailsService
    │   └── resources/
    │       ├── templates/        # login, register, dashboard, account, deposit, withdraw, transfer, transactions, error
    │       ├── static/css, static/js
    │       └── application.properties
    └── test/
```

## 1. Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8 running locally (or reachable) on port `3306`

## 2. Database Setup

The app auto-creates the schema (`spring.jpa.hibernate.ddl-auto=update`) and the database itself
(`createDatabaseIfNotExist=true`), so you only need a MySQL user with privileges. From the MySQL shell:

```sql
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON banking_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

(Adjust username/password to match your local MySQL setup, or override via environment
variables — see below. Never hardcode real credentials in source control.)

## 3. Configure Credentials

Credentials are externalized in `src/main/resources/application.properties` via environment
variables, with local defaults for convenience:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/banking_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
```

To use your own credentials without editing the file, export environment variables before running:

```bash
export DB_URL="jdbc:mysql://localhost:3306/banking_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
export DB_USERNAME=your_mysql_user
export DB_PASSWORD=your_mysql_password
```

## 4. Build & Run

From the `banking-app` directory:

```bash
mvn clean install
mvn spring-boot:run
```

Or run the packaged jar:

```bash
mvn clean package -DskipTests
java -jar target/banking-management-system-1.0.0.jar
```

The app starts on **http://localhost:8080**.

## 5. Using the App

1. Go to `http://localhost:8080/register` and create an account.
2. Log in at `http://localhost:8080/login`.
3. You'll land on `/dashboard` showing your auto-generated account number and balance.
4. Use the navbar to Deposit, Withdraw, Transfer, or view Transaction history.

## 6. Key Design Notes

- **Atomic transfers**: `TransactionServiceImpl.transfer()` is `@Transactional` and uses a
  pessimistic write lock (`findByAccountNumberForUpdate`) on the receiver account to prevent
  race conditions when two transfers hit the same account concurrently. If any step fails,
  the whole operation rolls back — no half-applied transfers.
- **Passwords**: hashed with BCrypt via Spring Security; plaintext passwords are never stored.
- **Security**: `/register` and `/login` are public; every other route requires authentication.
  CSRF protection is enabled by default (Thymeleaf automatically includes the token in forms
  via Spring Security's Thymeleaf integration).
- **Validation**: Bean Validation (`jakarta.validation`) annotations on DTOs and entities,
  enforced both in controllers (`@Valid`) and surfaced as inline field errors in the templates.
- **Error handling**: `GlobalExceptionHandler` catches domain exceptions
  (`InsufficientBalanceException`, `InvalidAccountException`, etc.) and renders a friendly
  `error.html` page instead of a stack trace.

## 7. Troubleshooting

| Problem | Fix |
|---|---|
| `Communications link failure` | MySQL isn't running or the URL/port is wrong. |
| `Access denied for user` | Check `DB_USERNAME` / `DB_PASSWORD` match a real MySQL user. |
| Port 8080 already in use | Change `server.port` in `application.properties`. |
| Login always fails | Confirm you registered first — the account/password are set at registration. |

---

Send Claude any compilation or runtime errors you hit and they'll be fixed directly in the source.
