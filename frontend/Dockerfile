## 1. Utiliser une image Nginx légère
#FROM nginx:alpine
#
## 2. Installer curl pour que le HEALTHCHECK de docker-compose fonctionne
#RUN apk add --no-cache curl
#
## 3. Copier les fichiers déjà compilés par Jenkins
#COPY dist/frontend/browser/ /usr/share/nginx/html
#
## 4. Copier la configuration Nginx
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#
## 5. AJOUTEZ CETTE LIGNE POUR FORCER LE CHANGEMENT
#RUN echo "Build Nginx v2"
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install -g @angular/cli && npm install
COPY . .
EXPOSE 4200

CMD [ "ng", "serve", "--host", "0.0.0.0", "--port", "4200" ]
