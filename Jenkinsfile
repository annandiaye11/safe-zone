pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        nodejs 'NodeJS-22'
    }

    triggers {
        // Déclencher un build toutes les nuits à 2h du matin
        cron('0 2 * * *')
        
        // Déclencher un build lors des commits (nécessite webhook configuré)
        githubPush()
    }

    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        BACKEND_SERVICES = 'api-gateway eureka-server user-service product-service media-service'
        FRONTEND_DIR = 'frontend'
        NOTIFICATION_EMAIL = 'annandiayr161@gmail.com'
        EUREKA_PORT = '8761'
        GATEWAY_PORT = '8080'
        USER_SERVICE_PORT = '8081'
        PRODUCT_SERVICE_PORT = '8082'
        MEDIA_SERVICE_PORT = '8083'
        BACKUP_DIR = '/tmp/jenkins-backups'
        DEPLOYMENT_TIMESTAMP = "${new Date().format('yyyyMMdd-HHmmss')}"
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
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code depuis Gitea...'
                git branch: 'main',
                    url: 'https://learn.zone01dakar.sn/git/annndiaye/mr-jenk.git',
                    credentialsId: 'gitea-credentials'
                echo '✅ Code récupéré avec succès'
            }
        }

        stage('Build Backend Services') {
            steps {
                echo '🔨 Compilation des microservices Spring Boot...'
                sh 'mvn clean install -DskipTests'
                echo '✅ Backend compilé avec succès'
            }
        }

        stage('Build Frontend') {
            steps {
                echo '🎨 Compilation du frontend Angular...'
                dir("${FRONTEND_DIR}") {
                    sh '''
                        export PATH="/opt/nodejs/v22.13.0/bin:$PATH"
                        echo "🔧 Utilisation de Node.js version: $(node --version)"
                        echo "📦 Utilisation de npm version: $(npm --version)"
                        npm install
                    '''
                    sh '''
                        export PATH="/opt/nodejs/v22.13.0/bin:$PATH"
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
                            '''
                            sh '''
                                export PATH="/opt/nodejs/v22.13.0/bin:$PATH"
                                npx ng test -- --watch=false --browsers=ChromeHeadless
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
                echo '🐳 Construction des images Docker...'
                sh 'docker-compose build'
                echo '✅ Images Docker construites'
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

                    // Création d'une sauvegarde avant déploiement
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
                    sleep(time: 45, unit: 'SECONDS')
                    try {
                        // Vérification Eureka Server
                        sh "curl -f http://localhost:${EUREKA_PORT}/actuator/health || exit 1"
                        echo '✅ Eureka Server est en vie'
                        
                        // Vérification API Gateway
                        sh "curl -f http://localhost:${GATEWAY_PORT}/actuator/health || exit 1"
                        echo '✅ API Gateway est en vie'
                        
                        // Vérification User Service
                        sh "curl -f http://localhost:${USER_SERVICE_PORT}/actuator/health || exit 1"
                        echo '✅ User Service est en vie'
                        
                        // Vérification Product Service  
                        sh "curl -f http://localhost:${PRODUCT_SERVICE_PORT}/actuator/health || exit 1"
                        echo '✅ Product Service est en vie'
                        
                        // Vérification Media Service
                        sh "curl -f http://localhost:${MEDIA_SERVICE_PORT}/actuator/health || exit 1"
                        echo '✅ Media Service est en vie'
                        
                        echo '✅ Tous les services sont opérationnels'
                    } catch (Exception e) {
                        echo '❌ Health check échoué'
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
                    emailext (
                        subject: "✅ BUILD SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                        body: """
                            <h2>✅ Build Réussi</h2>
                            <p><strong>Projet:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                            <p><strong>Environnement:</strong> ${params.ENVIRONMENT}</p>
                            <p><strong>Durée:</strong> ${currentBuild.durationString}</p>
                            <p><strong>URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        """,
                        to: "${NOTIFICATION_EMAIL}",
                        mimeType: 'text/html'
                    )
                    echo '📧 Email de succès envoyé'
                } catch (Exception e) {
                    echo "⚠️ Impossible d'envoyer l'email de succès: ${e.message}"
                }
            }
        }

        failure {
            echo '❌ Pipeline échoué !'
            script {
                try {
                    emailext (
                        subject: "❌ BUILD FAILED: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                        body: """
                            <h2>❌ Build Échoué</h2>
                            <p><strong>Projet:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                            <p><strong>Environnement:</strong> ${params.ENVIRONMENT}</p>
                            <p><strong>Durée:</strong> ${currentBuild.durationString}</p>
                            <p><strong>URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        """,
                        to: "${NOTIFICATION_EMAIL}",
                        mimeType: 'text/html'
                    )
                    echo '📧 Email d\'échec envoyé'
                } catch (Exception e) {
                    echo "⚠️ Impossible d'envoyer l'email d'échec: ${e.message}"
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
    echo '🐳 Déploiement avec Docker Compose...'
    sh '''
        # Arrêt des conteneurs existants
        docker-compose down || true
        
        # Nettoyage des conteneurs orphelins
        docker container prune -f || true
        
        # Démarrage avec build si nécessaire
        docker-compose up -d --build
        
        # Vérification que les conteneurs sont bien démarrés
        sleep 15
        docker-compose ps
    '''
    echo '✅ Déploiement Docker réussi'
}

def createBackup() {
    echo '💾 Création d\'une sauvegarde...'
    sh """
        # Création du répertoire de sauvegarde
        mkdir -p ${BACKUP_DIR}
        
        # Sauvegarde des JAR actuels
        if ls */target/*.jar 1> /dev/null 2>&1; then
            tar -czf ${BACKUP_DIR}/backup-${DEPLOYMENT_TIMESTAMP}.tar.gz */target/*.jar
            echo '✅ Sauvegarde des JAR créée'
        fi
        
        # Sauvegarde de la configuration Docker
        if [ -f docker-compose.yml ]; then
            cp docker-compose.yml ${BACKUP_DIR}/docker-compose-${DEPLOYMENT_TIMESTAMP}.yml
            echo '✅ Sauvegarde Docker Compose créée'
        fi
        
        # Conserver seulement les 5 dernières sauvegardes
        ls -t ${BACKUP_DIR}/backup-*.tar.gz | tail -n +6 | xargs -r rm
    """
}

def rollbackDeployment() {
    echo '🔄 Rollback du déploiement...'
    sh """
        # Récupération de la dernière sauvegarde
        LATEST_BACKUP=\$(ls -t ${BACKUP_DIR}/backup-*.tar.gz 2>/dev/null | head -n 1)
        
        if [ -n "\$LATEST_BACKUP" ]; then
            echo "Restauration depuis: \$LATEST_BACKUP"
            
            # Arrêt des services actuels
            docker-compose down || true
            pkill -f "java -jar" || true
            
            # Restauration des JAR
            tar -xzf "\$LATEST_BACKUP" -C ./
            
            # Redémarrage avec la version précédente
            if [ "${params.DEPLOY_DOCKER}" == "true" ]; then
                docker-compose up -d
            else
                deployLocally()
            fi
            
            echo '✅ Rollback terminé'
        else
            echo '❌ Aucune sauvegarde trouvée pour le rollback'
            error('No backup found for rollback')
        fi
    """
}

def deployLocally() {
    echo '🖥️ Déploiement local...'
    sh '''
        # Arrêt des services existants
        pkill -f "java -jar" || true
        pkill -f "ng serve" || true
        
        # Démarrage des services backend
        nohup java -jar eureka-server/target/*.jar --server.port=${EUREKA_PORT} > eureka.log 2>&1 &
        sleep 10
        nohup java -jar api-gateway/target/*.jar --server.port=${GATEWAY_PORT} > gateway.log 2>&1 &
        nohup java -jar user-service/target/*.jar --server.port=${USER_SERVICE_PORT} > user.log 2>&1 &
        nohup java -jar product-service/target/*.jar --server.port=${PRODUCT_SERVICE_PORT} > product.log 2>&1 &
        nohup java -jar media-service/target/*.jar --server.port=${MEDIA_SERVICE_PORT} > media.log 2>&1 &
    '''
    
    // Déploiement du frontend
    dir('frontend') {
        sh '''
            # Pour production, on peut servir les fichiers statiques via nginx
            # ou utiliser http-server au lieu de ng serve
            if command -v http-server &> /dev/null; then
                nohup http-server dist/frontend -p 4200 -a 0.0.0.0 > ../frontend.log 2>&1 &
            else
                # Fallback vers ng serve si http-server n'est pas disponible
                nohup npx ng serve --host 0.0.0.0 --port 4200 > ../frontend.log 2>&1 &
            fi
        '''
    }
    echo '✅ Déploiement local réussi'
}

