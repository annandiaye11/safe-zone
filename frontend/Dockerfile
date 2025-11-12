# ÉTAPE 1: Base du serveur web
# On utilise Nginx, un serveur web de production léger et rapide.
FROM nginx:alpine

# ÉTAPE 2: Copier les fichiers de l'application
# On copie les fichiers que Jenkins a déjà construits (étape 'Build Frontend')
# dans le dossier web par défaut de Nginx.
# Le build de 'ng build --configuration production' se trouve dans dist/frontend/
COPY dist/frontend/ /usr/share/nginx/html

# ÉTAPE 3: Copier la configuration Nginx
# On copie notre fichier de configuration personnalisé (de l'Étape 1)
# pour qu'il gère correctement le routage Angular.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Pas besoin de CMD ou EXPOSE, l'image Nginx de base s'en occupe.
# Elle expose le port 80 et démarre Nginx automatiquement.