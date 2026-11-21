# ADR-0002 : Front-end en React 18 + TypeScript + Vite

- **Statut** : proposé
- **Décision** : le front-end (`frontend/`) est une application React 18 en TypeScript (mode strict), construite avec Vite. Il ne parle qu'à `/api/...`, relayé vers l'API Gateway (proxy Vite en développement, nginx en production).
- **Pourquoi** :
  - Démarrage rapide : serveur de développement Vite quasi instantané, `npm run build` court en CI.
  - Peu de code de départ : un projet minimal tient en quelques fichiers, là où Angular impose CLI, modules/composants, injection de dépendances et RxJS dès le premier écran.
  - Typage de l'API : TypeScript permet de décrire les réponses des services (interfaces) et de détecter les erreurs de contrat à la compilation.
- **Alternative écartée** : Angular. Framework complet et très structuré, mais plus lourd à prendre en main et à configurer pour une équipe de 5 sur un projet académique où le front reste simple.
- **Conséquence** : le routage, l'authentification et la gestion d'état ne sont pas inclus ; ils seront ajoutés par des tickets dédiés (bibliothèques à choisir à ce moment-là). Le front est servi en production par nginx non-root (image `nginxinc/nginx-unprivileged`, port 8080).
