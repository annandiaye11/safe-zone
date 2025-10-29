# 🔧 Configuration Builds Distribués - Jenkins

## Ajout d'Agents Jenkins

### 1. Configuration d'un Agent Maven (pour Backend)

#### Sur la machine agent:
```bash
# Installation Java et Maven
sudo apt update
sudo apt install openjdk-17-jdk maven git

# Création utilisateur jenkins
sudo useradd -m -s /bin/bash jenkins-agent
sudo su - jenkins-agent

# Création répertoire de travail
mkdir -p ~/jenkins-workspace
```

#### Dans Jenkins Master:
1. Manage Jenkins → Manage Nodes and Clouds
2. New Node
   - Name: `maven-agent-01`
   - Type: `Permanent Agent`
3. Configuration:
   - Remote root directory: `/home/jenkins-agent/jenkins-workspace`
   - Labels: `maven backend java`
   - Launch method: `Launch agent via SSH`
   - Host: `IP_de_votre_agent`
   - Credentials: `jenkins-agent-ssh`

### 2. Configuration d'un Agent NodeJS (pour Frontend)

#### Sur la machine agent:
```bash
# Installation NodeJS
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
sudo apt-get install -y nodejs

# Installation Angular CLI
sudo npm install -g @angular/cli

# Vérification
node --version
npm --version
ng version
```

#### Dans Jenkins Master:
1. Nouveau node: `nodejs-agent-01`
2. Labels: `nodejs frontend angular`

### 3. Modification du Jenkinsfile pour Builds Distribués

```groovy
pipeline {
    agent none  // Pas d'agent par défaut
    
    tools {
        maven 'Maven-3.9'
        nodejs 'NodeJS-22'
    }
    
    stages {
        stage('Checkout') {
            agent any
            steps {
                echo '📥 Récupération du code...'
                git branch: 'main',
                    url: 'https://learn.zone01dakar.sn/git/annndiaye/mr-jenk.git',
                    credentialsId: 'gitea-credentials'
                
                // Archiver le workspace pour les autres agents
                stash name: 'source-code', includes: '**/*'
            }
        }
        
        stage('Build Backend Services') {
            agent {
                label 'maven && backend'
            }
            steps {
                echo '🔨 Compilation des microservices...'
                unstash 'source-code'
                sh 'mvn clean install -DskipTests'
                
                // Archiver les JARs pour les autres stages
                stash name: 'backend-jars', includes: '**/target/*.jar'
            }
        }
        
        stage('Build Frontend') {
            agent {
                label 'nodejs && frontend'
            }
            steps {
                echo '🎨 Compilation du frontend...'
                unstash 'source-code'
                dir('frontend') {
                    sh 'npm ci'
                    sh 'ng build --configuration production'
                }
                
                // Archiver le build frontend
                stash name: 'frontend-dist', includes: 'frontend/dist/**/*'
            }
        }
        
        stage('Tests Parallèles') {
            parallel {
                stage('Test Backend') {
                    agent {
                        label 'maven && backend'
                    }
                    steps {
                        unstash 'source-code'
                        script {
                            def services = ['api-gateway', 'eureka-server', 'user-service', 'product-service', 'media-service']
                            services.each { service ->
                                dir(service) {
                                    sh 'mvn test'
                                }
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
                    agent {
                        label 'nodejs && frontend'
                    }
                    steps {
                        unstash 'source-code'
                        dir('frontend') {
                            sh 'npm ci'
                            sh 'npm run test -- --watch=false --browsers=ChromeHeadless'
                        }
                    }
                }
                
                stage('Security Scan') {
                    agent {
                        label 'security'
                    }
                    steps {
                        unstash 'source-code'
                        // Exemple avec SonarQube
                        sh 'sonar-scanner'
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            agent {
                label 'docker'
            }
            steps {
                unstash 'source-code'
                unstash 'backend-jars'
                unstash 'frontend-dist'
                
                sh 'docker-compose build'
                
                // Push vers registry si nécessaire
                script {
                    if (params.ENVIRONMENT == 'production') {
                        sh 'docker-compose push'
                    }
                }
            }
        }
        
        stage('Deploy') {
            agent {
                label 'deployment'
            }
            steps {
                unstash 'source-code'
                unstash 'backend-jars'
                unstash 'frontend-dist'
                
                script {
                    if (params.DEPLOY_DOCKER) {
                        deployWithDocker()
                    } else {
                        deployLocally()
                    }
                }
            }
        }
    }
}
```

## Configuration Avancée

### 1. Load Balancing des Agents

```groovy
pipeline {
    agent {
        label 'maven'  // Jenkins choisira automatiquement un agent disponible
    }
    // ...
}
```

### 2. Agents Dynamiques avec Docker

```groovy
pipeline {
    agent {
        docker {
            image 'maven:3.9-openjdk-17'
            label 'docker'
        }
    }
    // ...
}
```

### 3. Agents Kubernetes (Bonus)

```groovy
pipeline {
    agent {
        kubernetes {
            yaml """
                apiVersion: v1
                kind: Pod
                spec:
                  containers:
                  - name: maven
                    image: maven:3.9-openjdk-17
                    command:
                    - sleep
                    args:
                    - 99d
                  - name: nodejs
                    image: node:22
                    command:
                    - sleep
                    args:
                    - 99d
            """
        }
    }
    // ...
}
```

## Monitoring des Agents

### 1. Script de monitoring
```bash
#!/bin/bash
# check-agents.sh

JENKINS_URL="http://localhost:8080"
JENKINS_USER="admin"
JENKINS_TOKEN="your-api-token"

# Vérifier le statut des agents
curl -s -u "$JENKINS_USER:$JENKINS_TOKEN" \
    "$JENKINS_URL/computer/api/json" | \
    jq '.computer[] | {name: .displayName, offline: .offline}'
```

### 2. Alertes automatiques
```groovy
// Dans un job de monitoring
pipeline {
    triggers {
        cron('*/5 * * * *')  // Toutes les 5 minutes
    }
    
    stages {
        stage('Check Agents') {
            steps {
                script {
                    def offlineAgents = Jenkins.instance.nodes.findAll { it.toComputer().offline }
                    
                    if (offlineAgents) {
                        emailext (
                            subject: "⚠️ Agents Jenkins Offline",
                            body: "Agents hors ligne: ${offlineAgents.collect { it.name }}",
                            to: "annandiayr161@gmail.com"
                        )
                    }
                }
            }
        }
    }
}
```

## Optimisations Performance

### 1. Cache Maven distribué
```groovy
// Utiliser un cache partagé Maven
sh 'mvn -Dmaven.repo.local=/shared/maven-cache clean install'
```

### 2. Cache NPM distribué
```groovy
// Frontend avec cache NPM
sh 'npm ci --cache /shared/npm-cache'
```

### 3. Workspace cleanup
```groovy
post {
    always {
        cleanWs()  // Nettoyer le workspace après chaque build
    }
}
```
