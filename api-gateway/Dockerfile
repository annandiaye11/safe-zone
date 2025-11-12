# 1. Utiliser une image Java de base (légère)
FROM eclipse-temurin:17-jdk-alpine

# (Optionnel) Installer 'curl' pour les health checks
RUN apk add --no-cache curl

# Définir le répertoire de travail
WORKDIR /app

# 2. Copier le .jar que Jenkins a DÉJÀ construit
# Le JAR se trouve dans le dossier 'target' du contexte de build
COPY target/*.jar app.jar

# 3. Démarrer l'application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]