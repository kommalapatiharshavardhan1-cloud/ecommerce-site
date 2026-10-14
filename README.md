# ShopEase E-Commerce Website

Full-stack e-commerce application built with **Java, Spring Boot, Neon Postgres, HTML, CSS, and JavaScript**.

## Features

- Product listings, search, categories, product details, shopping cart
- User registration, login, logout with JWT authentication
- Checkout, order placement, order history and order tracking
- Admin dashboard for products, categories, users, inventory, and orders
- REST APIs, Spring Data JPA, Neon (PostgreSQL) persistence
- Form validation, error handling, security (BCrypt, JWT, role-based access)
- Sample data seeding
- Unit and integration tests

## Project Structure

```
ecommerce-site/
├── pom.xml
├── neon.ts
├── src/main/java/com/ecommerce/...
├── src/main/resources/
│   ├── application.properties
│   └── static/ (HTML, CSS, JS)
├── src/test/...
└── README.md
```

## Prerequisites

- Java JDK 17 (or newer) - set `JAVA_HOME`
- Maven 3.8+ - must be on `PATH`
- Node.js 18+ (for Neon CLI)
- Neon CLI (`npm i -g neon`) - authenticated (`neon login`)

## Configuration

Neon environment variables are pulled into `.env.local` by `neon deploy`:

- `DATABASE_URL` (pooled Postgres URL, used automatically via JDBC conversion)
- `DATABASE_URL_UNPOOLED`
- `NEON_BRANCH`

You may also set:

| Variable | Default |
|---|---|
| SERVER_PORT | 8080 |
| DB_URL | JDBC URL override |
| DB_USERNAME | (empty / from URL) |
| DB_PASSWORD | (empty / from URL) |
| JWT_SECRET | change-me (change in production) |
| JWT_EXPIRATION_MS | 86400000 |
| ADMIN_EMAIL | admin@ecommerce.com |
| ADMIN_PASSWORD | Admin@123 |
| ADMIN_NAME | Administrator |

## Installation & Run (Windows / VS Code)

1. Install Neon CLI and authenticate:
   ```powershell
   npm i -g neon
   neon login
   ```
2. Link/deploy Neon (creates `.env.local`):
   ```powershell
   cd ecommerce-site
   neon init -y --no-agent-setup --org-id org-lively-mud-81908808 --project-id restless-rain-94393579 --branch production --services postgres
   neon deploy
   ```
3. Build and run:
   ```powershell
   mvn clean install
   mvn spring-boot:run
   ```
   Or run `EcommerceApplication` from VS Code.

4. Open http://localhost:8080

Default admin: `admin@ecommerce.com` / `Admin@123`

## Tests

```powershell
mvn test
```

Tests use an in-memory H2 database (profile `test`).

## Deployment Notes

- Use a strong `JWT_SECRET` (>= 256 bits).
- Neon pooled `DATABASE_URL` is used by default; prefer it for app traffic.
- Build a jar: `mvn package` then `java -jar target/ecommerce-site-1.0.0.jar`
- Place behind HTTPS reverse proxy.

## License

MIT
