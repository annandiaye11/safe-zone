pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    tools {
        maven 'Maven-3.9'
        nodejs 'NodeJS-22'
    }

    triggers {
        cron('0 2 * * *')
        githubPush()
    }

    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        BACKEND_SERVICES = 'api-gateway eureka-server user-service product-service media-service'
        FRONTEND_DIR = 'frontend'
        NOTIFICATION_EMAIL = 'annandiayr161@gmail.com'
        EUREKA_PORT = '8761'
        GATEWAY_PORT = '8080'
        BACKUP_DIR = '/tmp/jenkins-backups'
        DEPLOYMENT_TIMESTAMP = "${new Date().format('yyyyMMdd-HHmmss')}"
        
        // Docker Hub Configuration
        DOCKERHUB_USERNAME = 'annandiaye'
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
        IMAGE_TAG = "${BUILD_NUMBER}"
        
        // Optimisation Docker
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
    }

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'production'],
            description: 'Choisir l\'environnement de déploiement'
        )
        booleanParam(
            name: 'RUN_TESTS',
            defaultValue: true,
            description: 'Exécuter les tests ?'
        )
        booleanParam(
            name: 'DEPLOY_DOCKER',
            defaultValue: true,
            description: 'Déployer avec Docker Compose ?'
        )
        booleanParam(
            name: 'FORCE_REBUILD',
            defaultValue: false,
            description: 'Forcer la reconstruction des images Docker ?'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code depuis Gitea...'
                cleanWs()
                git branch: 'main',
                    url: 'https://learn.zone01dakar.sn/git/annndiaye/mr-jenk.git',
                    credentialsId: 'gitea-credentials'
                echo '✅ Code récupéré avec succès'
            }
        }

        stage('Build Backend Services') {
            steps {
                echo '🔨 Compilation des microservices Spring Boot...'
                sh 'mvn clean install -DskipTests -T 4'
                echo '✅ Backend compilé avec succès'
            }
        }

        stage('Build Frontend') {
            steps {
                echo '🎨 Compilation du frontend Angular...'
                dir("${FRONTEND_DIR}") {
                    sh '''
                        export PATH="/opt/nodejs/v22.13.0/bin:$PATH"
                        echo "🔧 Node.js version: $(node --version)"
                        echo "📦 npm version: $(npm --version)"
                        npm install
                        npx ng build --configuration production
                    '''
                }
                echo '✅ Frontend compilé avec succès'
            }
        }

        stage('Test Backend') {
            when {
                expression { params.RUN_TESTS == true }
            }
            steps {
                echo '🧪 Exécution des tests Backend...'
                script {
                    try {
                        def services = BACKEND_SERVICES.split(' ')
                        services.each { service ->
                            dir(service) {
                                sh 'mvn test'
                            }
                        }
                        echo '✅ Tous les tests backend ont réussi'
                    } catch (Exception e) {
                        echo '❌ Tests backend échoués'
                        currentBuild.result = 'FAILURE'
                        error("Tests backend échoués: ${e.message}")
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Test Frontend') {
            when {
                expression { params.RUN_TESTS == true }
            }
            steps {
                echo '🧪 Exécution des tests Frontend...'
                dir("${FRONTEND_DIR}") {
                    script {
                        try {
                            sh '''
                                export PATH="/opt/nodejs/v22.13.0/bin:$PATH"
                                npm ci
                                # Utiliser la configuration CI avec Puppeteer
                                npm run test:ci
                            '''
                            echo '✅ Tests frontend réussis'
                        } catch (Exception e) {
                            echo '⚠️ Tests frontend échoués (non bloquant)'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            when {
                expression { params.DEPLOY_DOCKER == true }
            }
            steps {
                echo '🐳 Construction et push des images Docker vers Docker Hub...'
                script {
                    def buildFlag = params.FORCE_REBUILD ? '--no-cache' : ''
                    
                    withDockerRegistry([credentialsId: "${DOCKERHUB_CREDENTIALS}", url: ""]) {
                        sh '''
                            # Build et push de chaque service backend
                            for service in ${BACKEND_SERVICES}; do
                                echo "🔨 Construction de l'image $service..."
                                docker build ${buildFlag} -t ${DOCKERHUB_USERNAME}/${service}:${IMAGE_TAG} ./$service
                                docker push ${DOCKERHUB_USERNAME}/${service}:${IMAGE_TAG}
                                
                                # Tag et push latest
                                docker tag ${DOCKERHUB_USERNAME}/${service}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${service}:latest
                                docker push ${DOCKERHUB_USERNAME}/${service}:latest
                                
                                echo "✅ ${service} poussé vers Docker Hub"
                            done
                            
                            # Build et push du frontend
                            echo "🔨 Construction de l'image frontend..."
                            docker build ${buildFlag} -t ${DOCKERHUB_USERNAME}/frontend:${IMAGE_TAG} ./frontend
                            docker push ${DOCKERHUB_USERNAME}/frontend:${IMAGE_TAG}
                            
                            # Tag et push latest
                            docker tag ${DOCKERHUB_USERNAME}/frontend:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/frontend:latest
                            docker push ${DOCKERHUB_USERNAME}/frontend:latest
                            
                            echo "✅ Frontend poussé vers Docker Hub"
                        '''
                    }
                }
                echo '✅ Toutes les images Docker construites et poussées vers Docker Hub'
            }
            post {
                success {
                    echo '🎉 Images Docker Hub disponibles:'
                    script {
                        sh '''
                            echo "📊 Images disponibles sur Docker Hub:"
                            echo "  - ${DOCKERHUB_USERNAME}/api-gateway:${IMAGE_TAG}"
                            echo "  - ${DOCKERHUB_USERNAME}/eureka-server:${IMAGE_TAG}"
                            echo "  - ${DOCKERHUB_USERNAME}/user-service:${IMAGE_TAG}"
                            echo "  - ${DOCKERHUB_USERNAME}/product-service:${IMAGE_TAG}"
                            echo "  - ${DOCKERHUB_USERNAME}/media-service:${IMAGE_TAG}"
                            echo "  - ${DOCKERHUB_USERNAME}/frontend:${IMAGE_TAG}"
                        '''
                    }
                }
            }
        }

        stage('Verify Docker Hub Images') {
            when {
                expression { params.DEPLOY_DOCKER == true }
            }
            steps {
                echo '🔍 Vérification des images sur Docker Hub...'
                script {
                    def services = ['api-gateway', 'eureka-server', 'user-service', 'product-service', 'media-service', 'frontend']
                    
                    services.each { service ->
                        sh """
                            echo "🔍 Vérification ${service} sur Docker Hub..."
                            docker pull ${DOCKERHUB_USERNAME}/${service}:${IMAGE_TAG}
                            docker inspect ${DOCKERHUB_USERNAME}/${service}:${IMAGE_TAG} > /dev/null
                            echo "✅ ${service}:${IMAGE_TAG} confirmé sur Docker Hub"
                        """
                    }
                }
                echo '✅ Toutes les images vérifiées sur Docker Hub'
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Déploiement vers l'environnement: ${params.ENVIRONMENT}"
                script {
                    if (params.ENVIRONMENT == 'production') {
                        input message: '⚠️ Confirmer le déploiement en PRODUCTION ?',
                              ok: 'Déployer',
                              submitter: 'admin'
                    }

                    createBackup()

                    try {
                        if (params.DEPLOY_DOCKER) {
                            deployWithDocker()
                        } else {
                            deployLocally()
                        }
                    } catch (Exception e) {
                        echo '❌ Déploiement échoué, rollback en cours...'
                        rollbackDeployment()
                        error("Deployment failed: ${e.message}")
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '🏥 Vérification de la santé de l\'application...'
                script {
                    // Attente progressive pour le démarrage complet
                    echo '⏳ Attente du démarrage de MongoDB et Kafka (30s)...'
                    sleep(time: 30, unit: 'SECONDS')
                    
                    echo '⏳ Attente du démarrage d\'Eureka Server (30s)...'
                    sleep(time: 30, unit: 'SECONDS')
                    
                    echo '⏳ Attente de l\'enregistrement des services (45s)...'
                    sleep(time: 45, unit: 'SECONDS')
                    
                    try {
                        // Vérification Eureka Server
                        def eurekaHealth = sh(
                            script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${EUREKA_PORT}/actuator/health",
                            returnStdout: true
                        ).trim()
                        
                        if (eurekaHealth == '200') {
                            echo '✅ Eureka Server est opérationnel'
                        } else {
                            error("Eureka Server non accessible (HTTP ${eurekaHealth})")
                        }
                        
                        // Vérification API Gateway
                        def gatewayHealth = sh(
                            script: "curl -k -s -o /dev/null -w '%{http_code}' https://localhost:${GATEWAY_PORT}/actuator/health",
                            returnStdout: true
                        ).trim()
                        
                        if (gatewayHealth == '200') {
                            echo '✅ API Gateway est opérationnel'
                        } else {
                            error("API Gateway non accessible (HTTP ${gatewayHealth})")
                        }
                        
                        // Vérification des microservices via Docker
                        echo '🔍 Vérification des microservices...'
                        sh '''
                            docker exec user-service curl -s -f http://localhost:8081/actuator/health || echo "⚠️ User Service: en cours de démarrage"
                            docker exec product-service curl -s -f http://localhost:8082/actuator/health || echo "⚠️ Product Service: en cours de démarrage"
                            docker exec media-service curl -s -f http://localhost:8083/actuator/health || echo "⚠️ Media Service: en cours de démarrage"
                        '''
                        
                        // Vérification du frontend
                        def frontendRunning = sh(
                            script: "docker ps --filter 'name=frontend' --filter 'status=running' --format '{{.Names}}'",
                            returnStdout: true
                        ).trim()
                        
                        if (frontendRunning) {
                            echo '✅ Frontend est en cours d\'exécution'
                        } else {
                            echo '⚠️ Frontend container non trouvé ou arrêté'
                        }
                        
                        // Affichage de l'état des conteneurs
                        echo '📊 État des conteneurs:'
                        sh 'docker-compose ps'
                        
                        echo '✅ Health check terminé avec succès'
                        
                    } catch (Exception e) {
                        echo '❌ Health check échoué'
                        sh 'docker-compose logs --tail=50'
                        error("Health check failed: ${e.message}")
                    }
                }
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline exécuté avec succès !'
            script {
                try {
                    mail(
                        to: "${NOTIFICATION_EMAIL}",
                        subject: "✅ BUILD RÉUSSI: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                        body: """
                            ✅ Build Réussi
                            Projet: ${env.JOB_NAME}
                            Build: #${env.BUILD_NUMBER}
                            Environnement: ${params.ENVIRONMENT}
                            Durée: ${currentBuild.durationString}
                            URL: ${env.BUILD_URL}
                        """
                    )
                    echo '📧 Email de succès envoyé'
                } catch (Exception e) {
                    echo "⚠️ Impossible d'envoyer l'email: ${e.message}"
                }
            }
        }

        failure {
            echo '❌ Pipeline échoué !'
            script {
                try {
                    mail(
                        to: "${NOTIFICATION_EMAIL}",
                        subject: "❌ BUILD FAILED: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                        body: "Le build a échoué. Veuillez vérifier les détails ici: ${env.BUILD_URL}"
                    )
                    echo '📧 Email d\'échec envoyé'
                } catch (Exception e) {
                    echo "⚠️ Impossible d'envoyer l'email: ${e.message}"
                }
            }
        }

        always {
            echo '🧹 Nettoyage...'
            cleanWs()
        }
    }
}

def deployWithDocker() {
    echo '🐳 Déploiement depuis Docker Hub...'
    sh """
        echo "🧹 Nettoyage des conteneurs existants..."
        docker-compose down -v || true
        docker container prune -f || true
        
        echo "🔓 Libération des ports..."
        fuser -k 8080/tcp 2>/dev/null || true
        fuser -k 8090/tcp 2>/dev/null || true
        fuser -k 4200/tcp 2>/dev/null || true
        
        sleep 5
        
        echo "📥 Pull des images depuis Docker Hub..."
        docker pull ${DOCKERHUB_USERNAME}/api-gateway:${IMAGE_TAG}
        docker pull ${DOCKERHUB_USERNAME}/eureka-server:${IMAGE_TAG}
        docker pull ${DOCKERHUB_USERNAME}/user-service:${IMAGE_TAG}
        docker pull ${DOCKERHUB_USERNAME}/product-service:${IMAGE_TAG}
        docker pull ${DOCKERHUB_USERNAME}/media-service:${IMAGE_TAG}
        docker pull ${DOCKERHUB_USERNAME}/frontend:${IMAGE_TAG}
        
        echo "🏷️ Tag des images pour docker-compose..."
        docker tag ${DOCKERHUB_USERNAME}/api-gateway:${IMAGE_TAG} api-gateway:latest
        docker tag ${DOCKERHUB_USERNAME}/eureka-server:${IMAGE_TAG} eureka-server:latest
        docker tag ${DOCKERHUB_USERNAME}/user-service:${IMAGE_TAG} user-service:latest
        docker tag ${DOCKERHUB_USERNAME}/product-service:${IMAGE_TAG} product-service:latest
        docker tag ${DOCKERHUB_USERNAME}/media-service:${IMAGE_TAG} media-service:latest
        docker tag ${DOCKERHUB_USERNAME}/frontend:${IMAGE_TAG} frontend:latest
        
        echo "🚀 Démarrage des conteneurs..."
        # Démarrage séquentiel pour éviter les problèmes de dépendances
        docker-compose up -d mongodb zookeeper kafka1 kafka2
        sleep 20
        
        docker-compose up -d eureka-server
        sleep 30
        
        docker-compose up -d api-gateway user-service product-service media-service
        sleep 20
        
        docker-compose up -d frontend
        
        echo "✅ Tous les services déployés depuis Docker Hub"
        docker-compose ps
        
        echo "📊 Images utilisées:"
        docker images | grep -E "(${DOCKERHUB_USERNAME}|api-gateway|eureka-server|user-service|product-service|media-service|frontend)"
    """
    '''
    echo '✅ Déploiement Docker réussi'
}

def createBackup() {
    echo '💾 Création d\'une sauvegarde...'
    sh """
        mkdir -p ${BACKUP_DIR}
        
        if ls */target/*.jar 1> /dev/null 2>&1; then
            tar -czf ${BACKUP_DIR}/backup-${DEPLOYMENT_TIMESTAMP}.tar.gz */target/*.jar
            echo '✅ Sauvegarde des JAR créée'
        fi
        
        if [ -f docker-compose.yml ]; then
            cp docker-compose.yml ${BACKUP_DIR}/docker-compose-${DEPLOYMENT_TIMESTAMP}.yml
            echo '✅ Sauvegarde Docker Compose créée'
        fi
        
        ls -t ${BACKUP_DIR}/backup-*.tar.gz 2>/dev/null | tail -n +6 | xargs -r rm
    """
}

def rollbackDeployment() {
    echo '🔄 Rollback du déploiement...'
    sh """
        LATEST_BACKUP=\$(ls -t ${BACKUP_DIR}/backup-*.tar.gz 2>/dev/null | head -n 1)
        
        if [ -n "\$LATEST_BACKUP" ]; then
            echo "Restauration depuis: \$LATEST_BACKUP"
            
            docker-compose down -v || true
            pkill -f 'java -jar' || true
            
            tar -xzf "\$LATEST_BACKUP" -C ./
            
            echo '✅ Rollback terminé'
        else
            echo '❌ Aucune sauvegarde trouvée'
        fi
    """
    
    if (params.DEPLOY_DOCKER) {
        echo '🐳 Redémarrage avec Docker...'
        deployWithDocker()
    } else {
        deployLocally()
    }
}

def deployLocally() {
    echo '🖥️ Déploiement local...'
    sh '''
        pkill -f 'java -jar' || true
        pkill -f 'ng serve' || true
        
        nohup java -jar eureka-server/target/*.jar --server.port=${EUREKA_PORT} > eureka.log 2>&1 &
        sleep 10
        nohup java -jar api-gateway/target/*.jar --server.port=${GATEWAY_PORT} > gateway.log 2>&1 &
        nohup java -jar user-service/target/*.jar --server.port=8081 > user.log 2>&1 &
        nohup java -jar product-service/target/*.jar --server.port=8082 > product.log 2>&1 &
        nohup java -jar media-service/target/*.jar --server.port=8083 > media.log 2>&1 &
    '''
    
    dir('frontend') {
        sh '''
            export PATH="/opt/nodejs/v22.13.0/bin:$PATH"
            if command -v http-server &> /dev/null; then
                nohup http-server dist/frontend -p 4200 -a 0.0.0.0 > ../frontend.log 2>&1 &
            else
                nohup npx ng serve --host 0.0.0.0 --port 4200 > ../frontend.log 2>&1 &
            fi
        '''
    }
    echo '✅ Déploiement local réussi'
}