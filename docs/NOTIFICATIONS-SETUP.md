# 🔔 Notifications Setup - Safe Zone SonarQube

## Slack Integration

### 1. Webhook Configuration
```bash
# Ajouter dans GitHub Secrets
SLACK_WEBHOOK_URL: https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
```

### 2. GitHub Action pour notifications Slack
```yaml
# .github/workflows/slack-notifications.yml
name: Slack Notifications
on:
  workflow_run:
    workflows: ["SonarQube Analysis - Safe Zone"]
    types: [completed]

jobs:
  notify:
    runs-on: ubuntu-latest
    steps:
      - name: Notify Slack on Quality Gate Failure
        if: ${{ github.event.workflow_run.conclusion == 'failure' }}
        uses: rtCamp/action-slack-notify@v2
        env:
          SLACK_WEBHOOK: ${{ secrets.SLACK_WEBHOOK_URL }}
          SLACK_MESSAGE: '🚨 SonarQube Quality Gate FAILED for Safe-Zone'
          SLACK_COLOR: danger
          
      - name: Notify Slack on Success  
        if: ${{ github.event.workflow_run.conclusion == 'success' }}
        uses: rtCamp/action-slack-notify@v2
        env:
          SLACK_WEBHOOK: ${{ secrets.SLACK_WEBHOOK_URL }}
          SLACK_MESSAGE: '✅ SonarQube Quality Gate PASSED for Safe-Zone'
          SLACK_COLOR: good
```

## Email Notifications

### SonarCloud Email Setup
1. Aller sur SonarCloud → Administration → General Settings
2. Configurer SMTP server
3. Activer notifications par email pour:
   - Quality Gate status changes
   - New security hotspots
   - New bugs/vulnerabilities

## IDE Integration

### VS Code SonarLint
```json
// .vscode/settings.json
{
  "sonarlint.connectedMode.project": {
    "connectionId": "sonarcloud",
    "projectKey": "annandiaye11_safe-zone"
  }
}
```

### IntelliJ IDEA SonarLint
1. Installer plugin SonarLint
2. Connecter à SonarCloud
3. Bind project: `annandiaye11_safe-zone`

### Configuration équipe
```bash
# Script setup développeurs
#!/bin/bash
echo "🔧 Configuration SonarLint pour Safe-Zone..."

# VS Code
if command -v code &> /dev/null; then
    code --install-extension SonarSource.sonarlint-vscode
    echo "✅ SonarLint VS Code installé"
fi

# IntelliJ (si installé)
if [ -d "$HOME/.local/share/JetBrains" ]; then
    echo "💡 Installer manuellement SonarLint dans IntelliJ"
    echo "   Settings → Plugins → SonarLint"
fi

echo "🔗 Connection SonarCloud: https://sonarcloud.io"
echo "📋 Project Key: annandiaye11_safe-zone"
```
