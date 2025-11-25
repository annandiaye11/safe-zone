# 🧪 Guide de Test SonarQube Local - Safe Zone

## 🚀 Démarrage rapide

### 1. Lancement des services
```bash
cd /home/anna/mr-jenk
docker-compose -f docker-compose-sonar.yml up -d
```

### 2. Vérification du statut
```bash
# Voir les conteneurs
docker-compose -f docker-compose-sonar.yml ps

# Voir les logs
docker-compose -f docker-compose-sonar.yml logs -f sonarqube
```

### 3. Accès à l'interface
- **URL** : http://localhost:9000
- **Identifiants par défaut** :
  - Username: `admin`
  - Password: `admin` (à changer au premier login)

## 🔧 Configuration initiale

### Première connexion
1. Ouvrir http://localhost:9000
2. Se connecter avec admin/admin
3. **Obligatoire** : Changer le mot de passe admin
4. Configurer un projet Safe-Zone local

### Création d'un projet local
```bash
# Dans SonarQube Web UI:
# 1. Cliquer "Create Project" → "Manually"
# 2. Project key: safe-zone-local
# 3. Display name: Safe Zone Local
# 4. Générer un token pour l'analyse
```

## 📊 Test d'analyse locale

### Méthode 1: Analyse Maven directe
```bash
cd /home/anna/mr-jenk

# Avec le token généré dans SonarQube
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=safe-zone-local \
  -Dsonar.projectName="Safe Zone Local" \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=YOUR_GENERATED_TOKEN
```

### Méthode 2: Script automatisé
Créer un script `analyze-local.sh` :
```bash
#!/bin/bash
echo "🔍 Analyse SonarQube locale de Safe-Zone..."

# Variables
SONAR_HOST="http://localhost:9000"
PROJECT_KEY="safe-zone-local"
PROJECT_NAME="Safe Zone Local"

# Vérifier que SonarQube est accessible
if ! curl -s "$SONAR_HOST/api/system/status" > /dev/null; then
    echo "❌ SonarQube n'est pas accessible sur $SONAR_HOST"
    echo "💡 Lancez: docker-compose -f docker-compose-sonar.yml up -d"
    exit 1
fi

echo "✅ SonarQube accessible"

# Demander le token si pas défini
if [ -z "$SONAR_TOKEN" ]; then
    echo "📝 Token SonarQube requis (généré dans l'interface web)"
    read -p "Entrez votre token: " SONAR_TOKEN
fi

# Lancer l'analyse
echo "🚀 Lancement de l'analyse..."
mvn clean verify sonar:sonar \
  -Dsonar.projectKey="$PROJECT_KEY" \
  -Dsonar.projectName="$PROJECT_NAME" \
  -Dsonar.host.url="$SONAR_HOST" \
  -Dsonar.login="$SONAR_TOKEN" \
  -Dsonar.java.coveragePlugin=jacoco \
  -Dsonar.coverage.jacoco.xmlReportPaths=**/target/site/jacoco/jacoco.xml

if [ $? -eq 0 ]; then
    echo "✅ Analyse terminée avec succès!"
    echo "📊 Voir les résultats: $SONAR_HOST/dashboard?id=$PROJECT_KEY"
else
    echo "❌ Erreur lors de l'analyse"
    exit 1
fi
```

## 🔍 Tests fonctionnels

### Test 1: Vérification de l'interface
- [ ] Accès à http://localhost:9000 ✅
- [ ] Login admin/admin réussi ✅
- [ ] Changement mot de passe obligatoire ✅
- [ ] Dashboard SonarQube visible ✅

### Test 2: Création de projet
- [ ] Bouton "Create Project" visible
- [ ] Projet "safe-zone-local" créé
- [ ] Token d'analyse généré
- [ ] Configuration projet complète

### Test 3: Analyse du code
- [ ] Commande Maven exécutée sans erreur
- [ ] Rapport d'analyse généré
- [ ] Métriques visibles dans le dashboard
- [ ] Issues détectées et affichées

### Test 4: Quality Gate
- [ ] Quality Gate "Sonar way" appliqué
- [ ] Résultat PASSED ou FAILED affiché
- [ ] Détails des conditions visibles

## 🚨 Dépannage

### Problème: SonarQube ne démarre pas
```bash
# Vérifier les logs
docker-compose -f docker-compose-sonar.yml logs sonarqube

# Redémarrer les services
docker-compose -f docker-compose-sonar.yml restart

# Nettoyer et relancer
docker-compose -f docker-compose-sonar.yml down
docker-compose -f docker-compose-sonar.yml up -d
```

### Problème: Erreur de connexion BDD
```bash
# Vérifier PostgreSQL
docker-compose -f docker-compose-sonar.yml logs sonarqube-db

# Recréer les volumes si nécessaire
docker-compose -f docker-compose-sonar.yml down -v
docker-compose -f docker-compose-sonar.yml up -d
```

### Problème: Analyse Maven échoue
```bash
# Vérifier la connectivité
curl -I http://localhost:9000

# Vérifier le token
curl -u YOUR_TOKEN: http://localhost:9000/api/authentication/validate

# Debug Maven
mvn sonar:sonar -X -Dsonar.host.url=http://localhost:9000
```

## 📋 Checklist complète

### Avant le test
- [ ] Docker et Docker Compose installés
- [ ] Port 9000 libre
- [ ] Projet Maven compilable

### Pendant le test  
- [ ] Services démarrés correctement
- [ ] Interface web accessible
- [ ] Projet créé dans SonarQube
- [ ] Token généré et sauvegardé

### Après le test
- [ ] Analyse exécutée avec succès
- [ ] Métriques visibles
- [ ] Quality Gate évalué
- [ ] Issues identifiées

### Nettoyage (optionnel)
```bash
# Arrêter les services
docker-compose -f docker-compose-sonar.yml down

# Supprimer les volumes (perte de données)
docker-compose -f docker-compose-sonar.yml down -v
```

## 🎯 Objectifs de validation

1. ✅ **Setup Docker** : SonarQube local opérationnel
2. ✅ **Configuration** : Projet Safe-Zone créé et configuré  
3. ✅ **Analyse** : Code analysé avec succès
4. ✅ **Reporting** : Métriques qualité visibles
5. ✅ **Quality Gate** : Évaluation automatique fonctionnelle

Une fois ces tests validés, votre setup SonarQube local est prêt pour le développement ! 🚀
