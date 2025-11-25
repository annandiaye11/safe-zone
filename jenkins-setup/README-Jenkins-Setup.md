# 🚀 Guide de Configuration Jenkins pour Buy-01

## 1. Installation de Jenkins

### Option A: Installation via Docker (Recommandée)
```bash
# Créer un réseau Docker pour Jenkins
docker network create jenkins

# Lancer Jenkins avec Docker
docker run -d \
  --name jenkins \
  --network jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts

# Récupérer le mot de passe initial
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Option B: Installation directe
```bash
# Ubuntu/Debian
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
echo deb https://pkg.jenkins.io/debian binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list
sudo apt update
sudo apt install jenkins
```

## 2. Configuration Initiale

### Accès à Jenkins
1. Ouvrir: `http://localhost:8080`
2. Entrer le mot de passe initial
3. Installer les plugins suggérés

### Plugins Essentiels à Installer
```
- Git Plugin
- Pipeline Plugin  
- Docker Pipeline Plugin
- Email Extension Plugin
- NodeJS Plugin
- Maven Integration Plugin
- GitHub Integration Plugin
- Blue Ocean (optionnel, pour une meilleure UI)
```

## 3. Configuration des Outils

### 3.1 Configuration Maven
1. Manage Jenkins → Global Tool Configuration
2. Maven → Add Maven
   - Name: `Maven-3.9`
   - Install automatically ✓
   - Version: 3.9.6

### 3.2 Configuration NodeJS
1. Manage Jenkins → Global Tool Configuration
2. NodeJS → Add NodeJS
   - Name: `NodeJS-22`
   - Install automatically ✓
   - Version: 22.x

### 3.3 Configuration Git
1. Manage Jenkins → Global Tool Configuration
2. Git → Add Git
   - Name: `Default`
   - Path to Git executable: `/usr/bin/git`

## 4. Configuration des Credentials

### 4.1 Credentials Git pour Gitea
1. Manage Jenkins → Manage Credentials
2. (global) → Add Credentials
3. Type: `Username with password`
4. ID: `gitea-credentials`
5. Username: `votre-username-gitea`
6. Password: `votre-password-gitea`

### 4.2 Configuration SMTP pour Email
1. Manage Jenkins → Configure System
2. E-mail Notification:
   - SMTP server: `smtp.gmail.com`
   - Use SMTP Authentication ✓
   - User Name: `annandiayr161@gmail.com`
   - Password: `app-password-gmail`
   - Use SSL ✓
   - SMTP Port: `465`

## 5. Création du Job Pipeline

### 5.1 Nouveau Job
1. New Item → Pipeline
2. Name: `buy-01-pipeline`

### 5.2 Configuration Pipeline
1. Pipeline → Definition: `Pipeline script from SCM`
2. SCM: `Git`
3. Repository URL: `https://learn.zone01dakar.sn/git/annndiaye/mr-jenk.git`
4. Credentials: `gitea-credentials`
5. Branch: `*/main`
6. Script Path: `Jenkinsfile`

### 5.3 Build Triggers
1. ✓ GitHub hook trigger for GITScm polling
2. ✓ Poll SCM (optionnel): `H/5 * * * *`

## 6. Configuration Webhook Gitea

### Sur votre repository Gitea:
1. Settings → Webhooks
2. Add Webhook → Gitea
3. Target URL: `http://your-jenkins-url:8080/gitea-webhook/post`
4. HTTP Method: `POST`
5. POST Content Type: `application/json`
6. Events: `Push events`

## 7. Test du Pipeline

```bash
# Test manuel
1. Aller sur Jenkins → buy-01-pipeline
2. Build with Parameters
3. Choisir Environment: dev
4. Build

# Vérifier les logs dans Console Output
```

## 8. Optimisations (Bonus)

### 8.1 Agents Distribués
```groovy
pipeline {
    agent {
        label 'linux && java11'
    }
    // ou
    stages {
        stage('Build Backend') {
            agent {
                label 'maven-agent'
            }
            // ...
        }
        stage('Build Frontend') {
            agent {
                label 'nodejs-agent'
            }
            // ...
        }
    }
}
```

### 8.2 Pipeline Parallèle
```groovy
stage('Tests') {
    parallel {
        stage('Backend Tests') {
            steps {
                // tests backend
            }
        }
        stage('Frontend Tests') {
            steps {
                // tests frontend  
            }
        }
    }
}
```

## 9. Monitoring et Maintenance

### Logs importants:
- Jenkins logs: `/var/jenkins_home/logs/`
- Build logs: Dans l'interface Jenkins
- Application logs: `/tmp/jenkins-backups/`

### Commandes utiles:
```bash
# Restart Jenkins
sudo systemctl restart jenkins

# Voir les processus Jenkins
ps aux | grep jenkins

# Nettoyer les workspaces
find /var/jenkins_home/workspace -name "*" -type d -mtime +7 -exec rm -rf {} \;
```

## 10. Troubleshooting

### Problèmes courants:
1. **Port 8080 occupé**: Changer le port Jenkins
2. **Permissions Docker**: Ajouter Jenkins au groupe docker
3. **OutOfMemory**: Augmenter la mémoire JVM
4. **Git authentication**: Vérifier les credentials

### Commandes de diagnostic:
```bash
# Vérifier Jenkins
curl -f http://localhost:8080

# Vérifier les outils
mvn --version
node --version
git --version
```
