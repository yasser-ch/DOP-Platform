# Étape 1 : build des fichiers statiques avec Node
FROM node:22-alpine AS build
WORKDIR /app
# On copie d'abord les manifestes : la couche npm ci reste en cache tant que les dépendances ne changent pas.
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Étape 2 : service des fichiers par nginx, sans droits root
FROM nginxinc/nginx-unprivileged:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 8080
