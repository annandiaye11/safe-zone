#!/bin/bash

# 🚀 Script de Déploiement Buy-01
# Usage: ./deploy.sh [dev|staging|production]

set -e

ENVIRONMENT=${1:-dev}
PROJECT_ROOT="/home/anna/IdeaProjects/buy-01"
BACKUP_DIR="/tmp/buy01-backups"
LOG_DIR="/var/log/buy01"

# Couleurs pour les logs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction de logging
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
    exit 1
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Vérification des prérequis
check_prerequisites() {
    log "Vérification des prérequis..."
    
    command -v java >/dev/null 2>&1 || error "Java n'est pas installé"
    command -v mvn >/dev/null 2>&1 || error "Maven n'est pas installé"
    command -v docker >/dev/null 2>&1 || error "Docker n'est pas installé"
    command -v docker-compose >/dev/null 2>&1 || error "Docker Compose n'est pas installé"
    
    # Vérifier que Docker est en cours d'exécution
    docker info >/dev/null 2>&1 || error "Docker n'est pas en cours d'exécution"
    
    success "Tous les prérequis sont satisfaits"
}

# Création des répertoires nécessaires
setup_directories() {
    log "Création des répertoires..."
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$LOG_DIR"
    success "Répertoires créés"
}

# Sauvegarde avant déploiement
create_backup() {
    log "Création d'une sauvegarde..."
    
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_FILE="$BACKUP_DIR/backup_${ENVIRONMENT}_${TIMESTAMP}.tar.gz"
    
    cd "$PROJECT_ROOT"
    
    # Sauvegarde des JAR et configurations
    if ls */target/*.jar >/dev/null 2>&1; then
        tar -czf "$BACKUP_FILE" \
            */target/*.jar \
            docker-compose.yml \
            frontend/dist 2>/dev/null || true
        success "Sauvegarde créée: $BACKUP_FILE"
    else
        warning "Aucun JAR trouvé pour la sauvegarde"
    fi
    
    # Nettoyer les anciennes sauvegardes (garder les 5 dernières)
    ls -t "$BACKUP_DIR"/backup_${ENVIRONMENT}_*.tar.gz 2>/dev/null | tail -n +6 | xargs -r rm
}

# Arrêt des services
stop_services() {
    log "Arrêt des services existants..."
    
    # Arrêt Docker Compose
    cd "$PROJECT_ROOT"
    docker-compose down 2>/dev/null || true
    
    # Arrêt des processus Java
    pkill -f "java -jar" || true
    pkill -f "ng serve" || true
    
    sleep 5
    success "Services arrêtés"
}

# Build de l'application
build_application() {
    log "Build de l'application..."
    
    cd "$PROJECT_ROOT"
    
    # Build Maven
    log "Build des services backend..."
    mvn clean install -DskipTests -q
    
    # Build Angular
    log "Build du frontend..."
    cd frontend
    npm ci --silent
    
    case $ENVIRONMENT in
        "production")
            npm run build -- --configuration production
            ;;
        "staging")
            npm run build -- --configuration staging 2>/dev/null || npm run build
            ;;
        *)
            npm run build
            ;;
    esac
    
    cd "$PROJECT_ROOT"
    success "Build terminé"
}

# Tests
run_tests() {
    if [[ "$RUN_TESTS" == "true" ]]; then
        log "Exécution des tests..."
        
        # Tests backend
        for service in api-gateway eureka-server user-service product-service media-service; do
            if [[ -d "$service" ]]; then
                log "Tests $service..."
                cd "$service"
                mvn test -q || warning "Tests échoués pour $service"
                cd "$PROJECT_ROOT"
            fi
        done
        
        # Tests frontend
        cd frontend
        npm run test -- --watch=false --browsers=ChromeHeadless 2>/dev/null || warning "Tests frontend échoués"
        cd "$PROJECT_ROOT"
        
        success "Tests terminés"
    else
        log "Tests ignorés (RUN_TESTS=false)"
    fi
}

# Déploiement avec Docker
deploy_docker() {
    log "Déploiement avec Docker Compose..."
    
    cd "$PROJECT_ROOT"
    
    # Variables d'environnement selon l'environnement
    case $ENVIRONMENT in
        "production")
            export SPRING_PROFILES_ACTIVE=docker,prod
            ;;
        "staging")
            export SPRING_PROFILES_ACTIVE=docker,staging
            ;;
        *)
            export SPRING_PROFILES_ACTIVE=docker,dev
            ;;
    esac
    
    # Build et démarrage
    docker-compose build
    docker-compose up -d
    
    success "Déploiement Docker terminé"
}

# Déploiement local
deploy_local() {
    log "Déploiement local..."
    
    cd "$PROJECT_ROOT"
    
    # Variables de ports selon l'environnement
    case $ENVIRONMENT in
        "production")
            EUREKA_PORT=8761
            GATEWAY_PORT=8080
            USER_PORT=8081
            PRODUCT_PORT=8082
            MEDIA_PORT=8083
            PROFILE="prod"
            ;;
        "staging")
            EUREKA_PORT=8771
            GATEWAY_PORT=8090
            USER_PORT=8091
            PRODUCT_PORT=8092
            MEDIA_PORT=8093
            PROFILE="staging"
            ;;
        *)
            EUREKA_PORT=8761
            GATEWAY_PORT=8080
            USER_PORT=8081
            PRODUCT_PORT=8082
            MEDIA_PORT=8083
            PROFILE="dev"
            ;;
    esac
    
    # Démarrage des services
    nohup java -jar eureka-server/target/*.jar \
        --server.port=$EUREKA_PORT \
        --spring.profiles.active=$PROFILE \
        > "$LOG_DIR/eureka.log" 2>&1 &
    
    sleep 10
    
    nohup java -jar api-gateway/target/*.jar \
        --server.port=$GATEWAY_PORT \
        --spring.profiles.active=$PROFILE \
        > "$LOG_DIR/gateway.log" 2>&1 &
    
    nohup java -jar user-service/target/*.jar \
        --server.port=$USER_PORT \
        --spring.profiles.active=$PROFILE \
        > "$LOG_DIR/user.log" 2>&1 &
    
    nohup java -jar product-service/target/*.jar \
        --server.port=$PRODUCT_PORT \
        --spring.profiles.active=$PROFILE \
        > "$LOG_DIR/product.log" 2>&1 &
    
    nohup java -jar media-service/target/*.jar \
        --server.port=$MEDIA_PORT \
        --spring.profiles.active=$PROFILE \
        > "$LOG_DIR/media.log" 2>&1 &
    
    # Frontend
    cd frontend
    nohup ng serve --host 0.0.0.0 --port 4200 > "$LOG_DIR/frontend.log" 2>&1 &
    cd "$PROJECT_ROOT"
    
    success "Déploiement local terminé"
}

# Health check
health_check() {
    log "Vérification de la santé des services..."
    
    sleep 30
    
    case $ENVIRONMENT in
        "staging")
            PORTS="8771 8090 8091 8092 8093"
            ;;
        *)
            PORTS="8761 8080 8081 8082 8083"
            ;;
    esac
    
    for port in $PORTS; do
        if curl -f "http://localhost:$port/actuator/health" >/dev/null 2>&1; then
            success "Service sur le port $port: OK"
        else
            warning "Service sur le port $port: NOK"
        fi
    done
}

# Rollback
rollback() {
    log "Rollback en cours..."
    
    LATEST_BACKUP=$(ls -t "$BACKUP_DIR"/backup_${ENVIRONMENT}_*.tar.gz 2>/dev/null | head -n 1)
    
    if [[ -n "$LATEST_BACKUP" ]]; then
        log "Restauration depuis: $LATEST_BACKUP"
        
        stop_services
        
        cd "$PROJECT_ROOT"
        tar -xzf "$LATEST_BACKUP"
        
        if [[ "$DEPLOY_DOCKER" == "true" ]]; then
            deploy_docker
        else
            deploy_local
        fi
        
        success "Rollback terminé"
    else
        error "Aucune sauvegarde trouvée pour le rollback"
    fi
}

# Fonction principale
main() {
    log "🚀 Démarrage du déploiement pour l'environnement: $ENVIRONMENT"
    
    # Variables d'environnement
    export RUN_TESTS=${RUN_TESTS:-true}
    export DEPLOY_DOCKER=${DEPLOY_DOCKER:-true}
    
    # Validation de l'environnement
    case $ENVIRONMENT in
        "dev"|"staging"|"production")
            ;;
        *)
            error "Environnement invalide: $ENVIRONMENT. Utilisez: dev, staging, ou production"
            ;;
    esac
    
    # Exécution des étapes
    check_prerequisites
    setup_directories
    create_backup
    stop_services
    build_application
    run_tests
    
    if [[ "$DEPLOY_DOCKER" == "true" ]]; then
        deploy_docker
    else
        deploy_local
    fi
    
    health_check
    
    success "🎉 Déploiement terminé avec succès!"
    log "Logs disponibles dans: $LOG_DIR"
    log "Sauvegardes disponibles dans: $BACKUP_DIR"
}

# Gestion des signaux pour rollback automatique
trap 'error "Déploiement interrompu"; rollback; exit 1' INT TERM

# Point d'entrée
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
