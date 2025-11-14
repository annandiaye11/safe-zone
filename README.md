# Mr-Jenk (CI/CD Pipeline avec Jenkins + Docker Hub)

### 📖 Description

Ce projet est une plateforme e-commerce complète basée sur une architecture de microservices développée avec Spring Boot et Angular, intégrée à une pipeline CI/CD Jenkins robuste avec déploiement Docker Hub.

**Fonctionnalités principales :**

- Inscription et authentification des utilisateurs (CLIENT OU VENDEUR)
- Gestion des produits par les vendeurs uniquement (CRUD complet)
- Gestion des médias (téléchargement et suppression d'images produits avec limite de 2 MB)  
- Interface Angular simple avec tableau de bord vendeur et catalogue produits public
- **Pipeline CI/CD Jenkins automatisée avec Docker Hub**
- **Tests automatisés (Backend Maven + Frontend Karma/Jasmine)**
- **Déploiement conteneurisé avec Docker**

L'objectif est de fournir une architecture sécurisée, évolutive et maintenable avec Spring Security JWT, Eureka pour la découverte de services, et une pipeline CI/CD complète pour l'intégration et le déploiement continus.

### 🛠️ Technologies

- **Backend:** Spring Boot, Spring Security, Spring Data MongoDB, Eureka, JWT
- **Frontend:** Angular 20, RxJS
- **Database:** MongoDB
- **Infrastructure:** Docker, Docker Compose, Jenkins, Docker Hub
- **CI/CD:** Jenkins Pipeline (Groovy), Maven, npm, SonarQube
- **Testing:** JUnit (Backend), Karma/Jasmine (Frontend), Puppeteer
- **Security:** JWT, HTTPS/SSL, Jenkins Credentials

### 📂 Architecture

```text
mr-jenk/
├── api-gateway/         # Gateway pour centraliser les appels aux microservices
├── eureka-server/       # Service Discovery (Eureka)
├── user-service/        # Gestion utilisateurs, rôles et profils
├── product-service/     # CRUD produits, gestion par vendeurs
├── media-service/       # Upload/gestion média (images produits)
├── frontend/            # Application Angular (UI)
├── jenkins-setup/       # Documentation et configuration Jenkins
├── scripts/            # Scripts de déploiement et utilitaires
├── Jenkinsfile          # Pipeline CI/CD Jenkins (Groovy)
├── docker-compose.yml   # Docker Compose pour développement local
├── pom.xml              # Parent Maven multi-module
└── README.md            # Documentation projet
```

### 🚀 Pipeline CI/CD

La pipeline Jenkins automatise le processus de build, test et déploiement :

#### **Étapes de la Pipeline :**

1. **🔄 Checkout** - Récupération du code depuis Gitea
2. **🔨 Build Backend** - Compilation Maven multi-module (4 threads parallèles)
3. **🎨 Build Frontend** - Compilation Angular avec npm/Node.js
4. **🧪 Test Backend** - Tests JUnit pour tous les microservices
5. **🧪 Test Frontend** - Tests Karma/Jasmine avec Puppeteer (headless Chrome)
6. **🐳 Build Docker Images** - Construction et push vers Docker Hub
7. **✅ Verify Docker Hub** - Vérification des images poussées
8. **🚀 Deploy** - Déploiement automatique (local ou Docker Hub)
9. **🏥 Health Check** - Vérification de la santé des services

#### **Configuration Docker Hub :**

- **Username:** `annandiaye`
- **Registry:** Docker Hub officiel
- **Images générées:**
  - `annandiaye/api-gateway:${BUILD_NUMBER}`
  - `annandiaye/eureka-server:${BUILD_NUMBER}`
  - `annandiaye/user-service:${BUILD_NUMBER}`
  - `annandiaye/product-service:${BUILD_NUMBER}`
  - `annandiaye/media-service:${BUILD_NUMBER}`
  - `annandiaye/frontend:${BUILD_NUMBER}`

### ⚙️ Fonctionnalités

#### 🔑 Utilisateurs (User Service)

- Inscription en tant que client ou vendeur.
- Authentification avec JWT.
- Gestion des profils utilisateurs.
- Téléchargement d'avatar pour les vendeurs.

#### 📦 Produits (Product Service)

- CRUD complet (Créer, Lire, Mettre à jour, Supprimer).
- Accessible uniquement aux vendeurs authentifiés.
- Association d'images avec les produits.
- Contrôle d'accès : un vendeur ne peut gérer que ses propres produits.

#### 🖼️ Médias (Media Service)

- Téléchargement sécurisé d'images (PNG, JPG, JPEG).
- Taille maximale : 2 MB.
- Validation backend et frontend.
- Suppression/modification des images associées aux produits.

#### 🌍 Frontend (Angular)

- Connexion / Inscription (avec gestion des rôles).
- Tableau de bord vendeur : gestion des produits et images.
- Catalogue produits public (sans recherche/filtre avancé).
- Gestion des erreurs (fichiers trop volumineux, mauvais format, etc.).

### 🔐 Sécurité

- Spring Security + JWT pour l'authentification et l'autorisation.
- Contrôle d'accès basé sur les rôles (RBAC) :
    - `ROLE_CLIENT` → lecture seule.
    - `ROLE_SELLER` → gestion des produits et médias.
- Mots de passe hachés et salés (BCrypt) avant stockage.
- Les APIs ne retournent jamais d'informations sensibles.
- Communications obligatoires via HTTPS (SSL/TLS).
- Accès strict : un vendeur ne peut modifier que ses propres produits.

### 🗄️ MongoDB

Chaque microservice dispose de sa propre base de données pour favoriser le découplage (pattern database per service).

#### 📌 Exemple : `user-service`

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

#### 📌 Exemple : `product-service`

```json
{
    "id": "uuid",
    "name": "Lenovo Legion 5",
    "description": "Ordinateur portable haute performance pour jeu et productivité",
    "price": 1200000.00,
    "quantite": 10,
    "sellerId": "uuid_user"
}
```

### 🚀 Lancement du Projet

#### 🔧 Prérequis

**Développement Local :**
- Java 17+
- Maven 3.9+
- Node.js 22+ / Angular CLI
- Docker & Docker Compose
- MongoDB

**Pipeline Jenkins :**
- Jenkins 2.4+ avec plugins : Pipeline, Docker, Git, NodeJS
- Compte Docker Hub configuré
- Identifiants Jenkins : `gitea-credentials`, `dockerhub-credentials`

#### **Étapes de Déploiement**

##### 1. Cloner le projet

```bash
git clone https://learn.zone01dakar.sn/git/annndiaye/mr-jenk.git
cd mr-jenk
```

##### 2. Option A : Déploiement via Pipeline Jenkins

```bash
# 1. Configurer Jenkins (voir jenkins-setup/README-Jenkins-Setup.md)
# 2. Créer une nouvelle Pipeline pointant vers ce repo
# 3. La pipeline se déclenche automatiquement sur git push
```

##### 3. Option B : Développement Local avec Docker Compose

```shell
# Lancer tous les services (MongoDB, Kafka, microservices)
docker-compose up --build -d

# Vérifier les logs
docker-compose logs -f
```

##### 4. Option C : Développement Local Manuel

```shell
# Backend (depuis la racine)
mvn clean install -DskipTests

# Frontend
cd frontend
npm install
npm start
```

##### 5. Accès à l'application

- **Application :** [http://localhost:4200](http://localhost:4200)
- **Eureka Dashboard :** [http://localhost:8761](http://localhost:8761)
- **API Gateway :** [https://localhost:8080](https://localhost:8080)

##### 6. Identifiants par défaut

- **Vendeur (Admin) :**
  - Email : `ftk@user.com`
  - Mot de passe : `Passer@123`
- **Client (Utilisateur) :**
  - Email : `johndoe@user.com`
  - Mot de passe : `Passer@123`

### 🐳 Gestion Docker

#### **Docker Compose (Développement Local)**

```shell
# Démarrer tous les services
docker-compose up -d

# Vérifier les logs
docker-compose logs -f

# Arrêter tous les services  
docker-compose down

# Supprimer volumes et données
docker-compose down -v
```

#### **Images Docker Hub (Production)**

```shell
# Pull des images depuis Docker Hub
docker pull annandiaye/api-gateway:latest
docker pull annandiaye/eureka-server:latest
docker pull annandiaye/user-service:latest
docker pull annandiaye/product-service:latest  
docker pull annandiaye/media-service:latest
docker pull annandiaye/frontend:latest

# Déploiement via Jenkins ou script
./scripts/deploy.sh
```

### 🔧 Configuration Jenkins

Voir le guide détaillé dans `jenkins-setup/README-Jenkins-Setup.md`

**Identifiants requis :**
- `gitea-credentials` : Accès au dépôt Git
- `dockerhub-credentials` : Nom d'utilisateur/Token Docker Hub

**Plugins Jenkins nécessaires :**
- Pipeline
- Git 
- Docker Pipeline
- NodeJS
- Maven Integration

### 📊 Monitoring & Métriques

**Health Checks automatiques :**

- Eureka Server : `http://localhost:8761/actuator/health`
- API Gateway : `https://localhost:8080/actuator/health` 
- Services : `http://localhost:808X/actuator/health`

**Pipeline Métriques :**

- Build time tracking
- Test coverage reports
- Docker image sizes
- Notifications email sur échec/succès

### 🚀 Auteurs & Contributeurs

[![GitHub](https://img.shields.io/badge/Anna%20Ndiaye-Lead%20DevOps-blue?style=for-the-badge&labelColor=green&logo=gitea&logoColor=darkgreen&color=blue)](https://learn.zone01dakar.sn/git/annndiaye)

**Spécialisations :**

- **Anna Ndiaye** : Architecture CI/CD, Jenkins Pipeline, Docker Hub Integration
- **Équipe Buy-01** : Architecture microservices, développement Spring Boot/Angular

**Projet Mr-Jenk :** Evolution CI/CD du projet Buy-01 avec focus sur DevOps et automation.
