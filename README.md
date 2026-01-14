# Interactive Tech Portfolio - Backend Implementation

A comprehensive Spring Boot backend demonstrating Clean Architecture, Design Patterns, and Database optimization techniques.

## 🏗️ Architecture Overview

```
prj2/
├── backend/                          # Spring Boot Application
│   ├── src/main/java/com/portfolio/
│   │   ├── domain/                   # Domain Layer (Entities, Value Objects)
│   │   │   ├── entity/               # JPA Entities with Inheritance
│   │   │   ├── valueobject/          # Immutable Value Objects
│   │   │   ├── repository/           # Repository Interfaces
│   │   │   ├── strategy/             # Strategy Pattern Implementation
│   │   │   └── factory/              # Factory Pattern Implementation
│   │   ├── application/              # Application Layer (Services)
│   │   │   ├── service/              # Business Logic Services
│   │   │   └── dto/                  # Data Transfer Objects
│   │   ├── infrastructure/           # Infrastructure Layer
│   │   │   ├── web/                  # REST Controllers
│   │   │   ├── persistence/          # JPA Repositories
│   │   │   └── config/               # Configuration Classes
│   │   └── shared/                   # Shared Components
│   │       └── exception/            # Custom Exceptions
│   └── src/main/resources/
│       ├── application.yml           # Application Configuration
│       └── db/migration/             # Flyway Migrations
├── database/                         # Database Scripts
│   ├── init.sql                      # Initial Schema
│   └── stored-procedures/            # PostgreSQL Functions
└── docker-compose.yml                # Docker Infrastructure
```

## 🚀 Features

### 1. Domain-Driven Design
- **Entity Inheritance**: User hierarchy (Admin, Member, Guest) using Single Table Inheritance
- **Value Objects**: Money, Email, UserId, OrderId with validation and immutability
- **Builder Pattern**: Order construction with validation

### 2. Design Patterns
- **Strategy Pattern**: PaymentStrategy with CreditCard, PayPay, BankTransfer implementations
- **Factory Pattern**: UserFactory for polymorphic user creation
- **Repository Pattern**: Abstract persistence with JPA adapters

### 3. Algorithm Visualization
- **Stack Visualizer**: Push/Pop operations with step tracking
- **Queue Visualizer**: Enqueue/Dequeue with FIFO demonstration
- **LinkedList Visualizer**: Insert/Delete with node visualization

### 4. Database Performance Lab
- **Query Comparison**: Optimized vs Unoptimized query analysis
- **Execution Plan Service**: EXPLAIN ANALYZE parsing
- **Index Optimization**: Composite index demonstrations

### 5. Code Display Service
- **Code Snippets**: JPA, Entity, Value Object examples
- **SQL Capture**: Hibernate query interception
- **Pattern Documentation**: Design pattern explanations

## 📋 Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.8+

## 🛠️ Getting Started

### 1. Start Infrastructure

```bash
# Start PostgreSQL, Redis, RabbitMQ
docker-compose up -d postgres redis rabbitmq

# Optional: Include pgAdmin for DB management
docker-compose --profile dev up -d
```

### 2. Run Application

```bash
cd backend
./mvnw spring-boot:run
```

### 3. Access APIs

- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Status**: http://localhost:8080/api/v1/status
- **Showcase**: http://localhost:8080/api/v1/showcase

## 📚 API Endpoints

### Algorithm Visualization
```
GET  /api/v1/algorithms              # List available algorithms
GET  /api/v1/algorithms/stack/demo   # Stack demo
GET  /api/v1/algorithms/queue/demo   # Queue demo
GET  /api/v1/algorithms/linkedlist/demo # LinkedList demo
POST /api/v1/algorithms/stack/execute   # Execute custom stack operations
```

### Code Lab
```
GET  /api/v1/code-lab                # Available resources
GET  /api/v1/code-lab/snippets       # All code snippets
GET  /api/v1/code-lab/patterns       # Design pattern documentation
POST /api/v1/code-lab/sql-capture/start  # Start SQL capture
```

### Database Lab
```
GET  /api/v1/db-lab/scenarios        # Available optimization scenarios
GET  /api/v1/db-lab/scenarios/{key}/compare  # Query comparison
GET  /api/v1/db-lab/tips             # Optimization tips
```

## 🔧 Configuration

### Environment Variables
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=portfolio
DB_USERNAME=portfolio_user
DB_PASSWORD=portfolio_pass
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
SERVER_PORT=8080
```

### Profiles
- `default`: Development configuration
- `staging`: Staging environment
- `production`: Production optimizations

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with test containers
./mvnw test -Dspring.profiles.active=test
```

## 📦 Docker Deployment

```bash
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

## 🏛️ Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.2 |
| Language | Java 21 |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Message Queue | RabbitMQ 3 |
| ORM | Hibernate/JPA |
| Migration | Flyway |
| Documentation | OpenAPI 3 / Swagger |

## 📝 Code Quality Rules

This project follows Rikkeisoft D5 coding standards:
- JSDoc comments on all public methods
- camelCase for variables and methods
- Const by default, let when needed
- Max function length: 20-30 lines
- Max nesting depth: 3-4 levels

## 📄 License

This project is for portfolio demonstration purposes.

## 👤 Author

Portfolio Development Team
