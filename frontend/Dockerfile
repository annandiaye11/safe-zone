# 1. Utiliser une image Nginx légère
FROM nginx:alpine

# 2. Installer curl pour que le HEALTHCHECK de docker-compose fonctionne
RUN apk add --no-cache curl

# 3. Copier les fichiers déjà compilés par Jenkins
# Le stage "Build Frontend" de Jenkins a créé ce dossier
COPY dist/frontend/browser/ /usr/share/nginx/html

# 4. Copier la configuration Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf