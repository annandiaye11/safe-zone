#!/bin/bash

# Script pour lancer les tests avec Puppeteer Chrome
# Détecte automatiquement le chemin Chrome de Puppeteer

set -e

echo "🔍 Recherche du Chrome de Puppeteer..."

# Trouver le chemin du Chrome de Puppeteer
PUPPETEER_CHROME=$(node -e "console.log(require('puppeteer').executablePath())" 2>/dev/null || echo "")

if [ -n "$PUPPETEER_CHROME" ] && [ -f "$PUPPETEER_CHROME" ]; then
    echo "✅ Chrome Puppeteer trouvé: $PUPPETEER_CHROME"
    export CHROME_BIN="$PUPPETEER_CHROME"
elif [ -f "/usr/bin/chromium-browser" ]; then
    echo "✅ Utilisation de Chromium local: /usr/bin/chromium-browser"
    export CHROME_BIN="/usr/bin/chromium-browser"
elif [ -f "/usr/bin/google-chrome" ]; then
    echo "✅ Utilisation de Chrome local: /usr/bin/google-chrome"
    export CHROME_BIN="/usr/bin/google-chrome"
else
    echo "❌ Aucun navigateur trouvé"
    exit 1
fi

echo "🧪 Lancement des tests avec CHROME_BIN=$CHROME_BIN"

# Lancer les tests
npx ng test --karma-config karma.conf.ci.js --watch=false --browsers=ChromeHeadlessPuppeteer
