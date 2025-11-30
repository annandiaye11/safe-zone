#!/bin/bash

# Script pour lancer l'analyse SonarQube du projet safe-zone
# Date: 30 Novembre 2025

echo "🚀 Lancement de l'analyse SonarQube pour safe-zone"
echo "=================================================="
echo ""

# Vérifier que SonarQube est accessible
echo "📡 Vérification de la connexion à SonarQube..."
if curl -s http://localhost:9000/api/system/status | grep -q "UP"; then
    echo "✅ SonarQube est accessible sur http://localhost:9000"
else
    echo "❌ SonarQube n'est pas accessible. Assurez-vous qu'il est démarré."
    echo "   Commande pour démarrer SonarQube:"
    echo "   docker-compose -f docker-compose-sonar.yml up -d"
    exit 1
fi

echo ""
echo "🧪 Lancement des tests et analyse..."
echo ""

# Lancer l'analyse Maven avec SonarQube
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=safe-zone \
  -Dsonar.projectName='safe-zone' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_c980d89c80d30372406d89341883c2f7502f4f41

# Vérifier le résultat
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Analyse terminée avec succès!"
    echo ""
    echo "📊 Résultats disponibles sur:"
    echo "   http://localhost:9000/dashboard?id=safe-zone"
    echo ""
    echo "📈 Métriques attendues:"
    echo "   - Tests exécutés: 33+"
    echo "   - Couverture de code: amélio rée"
    echo "   - Code dupliqué: réduit"
    echo ""
else
    echo ""
    echo "❌ Erreur lors de l'analyse SonarQube"
    echo "   Vérifiez les logs ci-dessus pour plus de détails"
    exit 1
fi

