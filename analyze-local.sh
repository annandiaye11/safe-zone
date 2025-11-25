#!/bin/bash
# 🔍 Script d'analyse SonarQube locale pour Safe-Zone

set -e  # Arrêt en cas d'erreur

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables
SONAR_HOST="http://localhost:9000"
PROJECT_KEY="safe-zone-local"
PROJECT_NAME="Safe Zone Local"

echo -e "${BLUE}🔍 Analyse SonarQube locale de Safe-Zone...${NC}"
echo "==============================================="

# Vérifier que SonarQube est accessible
echo -e "${YELLOW}📡 Vérification de la connectivité SonarQube...${NC}"
if ! curl -s "$SONAR_HOST/api/system/status" > /dev/null; then
    echo -e "${RED}❌ SonarQube n'est pas accessible sur $SONAR_HOST${NC}"
    echo -e "${YELLOW}💡 Lancez d'abord: docker-compose -f docker-compose-sonar.yml up -d${NC}"
    exit 1
fi

echo -e "${GREEN}✅ SonarQube accessible${NC}"

# Vérifier les services Docker
echo -e "${YELLOW}🐳 Vérification des conteneurs Docker...${NC}"
if ! docker-compose -f docker-compose-sonar.yml ps | grep -q "Up"; then
    echo -e "${RED}❌ Les services SonarQube ne sont pas tous démarrés${NC}"
    echo -e "${YELLOW}💡 Vérifiez avec: docker-compose -f docker-compose-sonar.yml ps${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Conteneurs Docker opérationnels${NC}"

# Demander le token si pas défini
if [ -z "$SONAR_TOKEN" ]; then
    echo ""
    echo -e "${YELLOW}📝 Token SonarQube requis${NC}"
    echo "   1. Ouvrez http://localhost:9000"
    echo "   2. Connectez-vous (admin/admin par défaut)"
    echo "   3. Allez dans My Account > Security > Generate Token"
    echo ""
    read -p "Entrez votre token SonarQube: " SONAR_TOKEN
fi

# Valider le token
echo -e "${YELLOW}🔐 Validation du token...${NC}"
if ! curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/authentication/validate" | grep -q "valid.*true"; then
    echo -e "${RED}❌ Token SonarQube invalide${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Token valide${NC}"

# Nettoyer et compiler le projet
echo -e "${YELLOW}🧹 Nettoyage et compilation du projet...${NC}"
mvn clean compile test-compile

echo -e "${YELLOW}🧪 Exécution des tests avec couverture...${NC}"
mvn test jacoco:report

# Lancer l'analyse SonarQube
echo ""
echo -e "${BLUE}🚀 Lancement de l'analyse SonarQube...${NC}"
echo "================================================="

mvn sonar:sonar \
  -Dsonar.projectKey="$PROJECT_KEY" \
  -Dsonar.projectName="$PROJECT_NAME" \
  -Dsonar.host.url="$SONAR_HOST" \
  -Dsonar.login="$SONAR_TOKEN" \
  -Dsonar.java.coveragePlugin=jacoco \
  -Dsonar.coverage.jacoco.xmlReportPaths=**/target/site/jacoco/jacoco.xml \
  -Dsonar.junit.reportPaths=**/target/surefire-reports \
  -Dsonar.exclusions="**/target/**,**/node_modules/**" \
  -Dsonar.verbose=true

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ Analyse terminée avec succès!${NC}"
    echo "================================================="
    echo -e "${BLUE}📊 Voir les résultats: ${SONAR_HOST}/dashboard?id=${PROJECT_KEY}${NC}"
    echo ""
    
    # Récupérer le statut de Quality Gate
    echo -e "${YELLOW}🎯 Vérification de la Quality Gate...${NC}"
    QG_STATUS=$(curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/qualitygates/project_status?projectKey=$PROJECT_KEY" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    
    if [ "$QG_STATUS" = "OK" ]; then
        echo -e "${GREEN}✅ Quality Gate: PASSED${NC}"
    elif [ "$QG_STATUS" = "ERROR" ]; then
        echo -e "${RED}❌ Quality Gate: FAILED${NC}"
        echo -e "${YELLOW}💡 Consultez le dashboard pour voir les problèmes à corriger${NC}"
    else
        echo -e "${YELLOW}⏳ Quality Gate: En cours d'évaluation...${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}🎉 Analyse locale complète!${NC}"
    echo "Ouvrez votre navigateur sur: $SONAR_HOST"
    
else
    echo ""
    echo -e "${RED}❌ Erreur lors de l'analyse SonarQube${NC}"
    echo -e "${YELLOW}💡 Vérifiez les logs Maven ci-dessus pour plus de détails${NC}"
    exit 1
fi
