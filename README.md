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

## Public Deployment (Manual Cloud Steps)

A `Dockerfile` is included for cloud hosting providers that support Java containers (Render, Railway, Fly.io, or similar).

### 1. Prepare GitHub repository
1. Do not commit `.env.local`, `.env`, or `.neon` (they are ignored).
2. Commit source code, `pom.xml`, frontend files, and the `Dockerfile`.
3. Push the repository to GitHub.

### 2. Create hosting account and project
1. Create an account with a provider that supports Docker/Java 17 (Render is beginner-friendly).
2. Choose "New Web Service" (or equivalent) and connect your GitHub repository.
3. Select the free tier if available.

### 3. Configure environment variables
Add these on the host:

- `DATABASE_URL`: pooled Neon Postgres URL from your Neon project.
- `JWT_SECRET`: a long random secret.
- `SERVER_PORT`: keep `8080` (or use the value required by your provider).
- `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_NAME`: your admin credentials.
- Optional: `JWT_EXPIRATION_MS`.

### 4. Build/start settings
- Build command: `mvn -DskipTests package`
- Start command: `java -jar target/ecommerce-site-1.0.0.jar`
- Or use the included `Dockerfile`.
- Health check path: `/actuator/health`

### 5. Get the public URL
After deployment succeeds, open the generated HTTPS URL, then test:
- `/`
- Product images
- `/actuator/health`
- Cart and demo checkout

### Current Verification Status
- Local server verified at `http://localhost:8080`.
- Local `/actuator/health` returns `UP`.
- Product catalog verified with 54 products.
- Product SVG images verified with HTTP 200.
- Public HTTPS URL has not been claimed because cloud account access is not available from this environment. Follow the steps above to publish.
## License

MIT
