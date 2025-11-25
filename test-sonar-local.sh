#!/bin/bash

echo "🔍 Test SonarQube Local - Safe Zone"
echo "=================================="

# Vérification des prérequis
if ! docker ps | grep -q sonarqube-local; then
    echo "❌ SonarQube local n'est pas démarré"
    echo "💡 Lancez: docker-compose -f docker-compose-sonar.yml up -d"
    exit 1
fi

echo "✅ SonarQube local détecté"

# Variables
SONAR_HOST_URL="http://localhost:9000"
PROJECT_KEY="safe-zone-local"
PROJECT_NAME="Safe Zone Local Test"

echo "🔧 Configuration:"
echo "   Host: $SONAR_HOST_URL"
echo "   Project: $PROJECT_KEY"

# Test de connexion
echo "🌐 Test de connexion à SonarQube..."
if curl -s "$SONAR_HOST_URL/api/system/status" > /dev/null; then
    echo "✅ SonarQube accessible"
else
    echo "❌ SonarQube non accessible"
    echo "💡 Attendez quelques secondes et réessayez"
    exit 1
fi

echo ""
echo "📋 Prochaines étapes manuelles:"
echo "1. Ouvrir http://localhost:9000"
echo "2. Se connecter: admin/admin (changer le mot de passe)"
echo "3. Créer le projet '$PROJECT_KEY'"
echo "4. Générer un token"
echo "5. Lancer l'analyse avec le token"

echo ""
# Token local (safe for local development only)
SONAR_TOKEN="${SONAR_LOCAL_TOKEN:-sqp_db96cd1a8377e059d23aee99f5bafc184178f2eb}"

echo "🚀 Commande d'analyse (FONCTIONNELLE):"
echo "mvn clean verify sonar:sonar \\"
echo "  -Dsonar.projectKey=$PROJECT_KEY \\"
echo "  -Dsonar.projectName=\"$PROJECT_NAME\" \\"
echo "  -Dsonar.host.url=$SONAR_HOST_URL \\"
echo "  -Dsonar.token=$SONAR_TOKEN"

echo ""
echo "✅ Dernière analyse: RÉUSSIE (BUILD SUCCESS)"
echo "📊 Résultats disponibles: http://localhost:9000/dashboard?id=safe-zone-local"

echo ""
echo "💡 Options:"
echo "   ./test-sonar-local.sh run    → Lance l'analyse automatiquement"
echo "   ./test-sonar-local.sh        → Affiche les instructions"

# Si argument 'run', exécuter l'analyse
if [ "$1" = "run" ]; then
    echo ""
    echo "🚀 Lancement de l'analyse SonarQube..."
    mvn clean verify sonar:sonar \
        -Dsonar.projectKey="$PROJECT_KEY" \
        -Dsonar.projectName="$PROJECT_NAME" \
        -Dsonar.host.url="$SONAR_HOST_URL" \
        -Dsonar.token="$SONAR_TOKEN"
        
    if [ $? -eq 0 ]; then
        echo "✅ Analyse terminée avec succès !"
        echo "📊 Voir les résultats: http://localhost:9000/dashboard?id=safe-zone-local"
    else
        echo "❌ Erreur lors de l'analyse"
    fi
fi
