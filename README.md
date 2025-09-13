# Buy 01 (Spring Boot Microservice + Angular)

### 📖 Description

This application is a simple e-commerce platform e2e based on an architecture of microservices developed with Spring
Boot and Angular.

It enables:

- Registration and Authentication of users (CLIENT OR SELLER)
- Product management by sellers only (full CRUD)
- Media management (uploading and deleting product images with a 2 MB limit)
- A simple Angular interface with a seller dashboard and public product catalog

The goal is to provide a secure, scalable, and maintainable architecture using Spring Security with JWT, Eureka for
service discovery, and possibly Kafka for inter-service communication

### 🛠️ Technologies

- `Backend:` Spring Boot, Spring Security, Spring Data MongoDB, Kafka (optional), Eureka, JWT
- `Frontend:` Angular 20, RxJS
- `Database:` MongoDB
- `Infrastructure:` Docker, Docker Compose
- `Security:` JWT, HTTPS/SSL

### 📂 Architecture

```text
buy-01/
├── api-gateway/         # Gateway for centralising calls to microservices
├── eureka-server/       # Service Discovery (Eureka)
├── user-service/        # User, role and profile management
├── product-service/     # Product CRUD, management by sellers
├── media-service/       # Media upload/management (product images)
├── frontend/            # Angular application (UI)
├── pom.xml              # Parent Maven
└── docker-compose.yml   # Docker Compose
```

### ⚙️ Features

#### 🔑 Users (User Service)

- Registration as a customer or seller.
- Authentication with JWT.
- User profile management.
- Avatar upload for sellers.

#### 📦 Products (Product Service)

- Full CRUD (Create, Read, Update, Delete).
- Only accessible to authenticated sellers.
- Association of images with products.
- Access control: a seller can only manage their own products.

#### 🖼️ Media (Media Service)

- Secure image upload (PNG, JPG, JPEG).
- Maximum size: 2 MB.
- Backend and frontend validation.
- Deletion/modification of images associated with products.

#### 🌍 Frontend (Angular)

- Sign In / Sign Up (with role management).
- Seller dashboard: product and image management.
- Public product catalogue (without advanced search/filter).
- Error management (files too large, wrong format, etc.).

### 🔐 Security

- Spring Security + JWT for authentication and authorisation.
- Role-based access control (RBAC):
    - `ROLE_CLIENT` → view only.
    - `ROLE_SELLER` → product and media management.
- Passwords are hashed and salted (BCrypt) before storage.
- APIs never return sensitive information.
- Communications must go through HTTPS (SSL/TLS).
- Strict access: a seller can only modify their own products.

### 🗄️ MongoDB

Each microservice has its own database to promote decoupling (database per service pattern).

#### 📌 Example : `user-service`

```json
{
    "id": "uuid",
    "name": "John DOE",
    "email": "john@example.com",
    "password": "hashed_password",
    "role": "SELLER",
    "avatar": "/media/avatar123.png"
}
```

#### 📌 Example : `product-service`

```json
{
    "id": "uuid",
    "name": "Lenovo Legion 5",
    "description": "High-performance laptop for gaming and productivity",
    "price": 1200000.00,
    "quantite": 10,
    "sellerId": "uuid_user"
}
```

### 🚀 Project launch

#### 🔧 Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 22+ / Angular CLI
- Docker & Docker Compose
- MongoDB (local and hosted)

#### Steps

##### 1. Clone the project

```bash
git clone https://learn.zone01dakar.sn/git/fmokomba/buy-01.git
cd buy-01
```

##### 2. Configure environment variables

```shell
cp .env.example .env
```

##### 3. Launch microservices with Docker Compose

```shell
docker-compose up --build
```

##### 4. Launch the frontend

```shell
npm install
npm start
```

##### 5. Access the application

- 👉 [http://localhost:4200](http://localhost:4200)
- 👉 [http://127.0.0.1:4200](http://127.0.0.1:4200)

##### 6. Default credentials
- Seller (Admin):
  - Email: `ftk@user.com`
  - Password: `Passer@123`
- Client (User):
    - Email: `johndoe@user.com`
    - Password: `Passer@123`

### Docker Compose

#### 1. start all services

```shell
docker-compose up -d
# OR
docker compose up -d
```

#### 2. Check logs

```shell
docker-compose logs -f
# OR
docker compose logs -f
```

#### 3. Stop all services

```shell
docker-compose down
# OR
docker compose down
```

#### 5 Delete volumes

```shell
docker-compose down -v
# OR
docker compose down -v
```

### Authors

[![GitHub](https://img.shields.io/badge/Fatima%20Keita-FTK?style=for-the-badge&labelColor=green&logo=gitea&logoColor=darkgreen&color=white)](https://learn.zone01dakar.sn/git/fakeita)\
[![GitHub](https://img.shields.io/badge/Anna%20Ndiaye-ANN?style=for-the-badge&labelColor=green&logo=gitea&logoColor=darkgreen&color=white)](https://learn.zone01dakar.sn/git/annndiaye)\
[![GitHub](https://img.shields.io/badge/Franchis%20Janel%20MOKOMBA-JAM?style=for-the-badge&labelColor=green&logo=gitea&logoColor=darkgreen&color=white)](https://learn.zone01dakar.sn/git/fmokomba)