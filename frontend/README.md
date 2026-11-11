# frontend

Interface web de la plateforme DOP : React 18 + TypeScript (strict) + Vite.
Choix documenté dans [`docs/decisions/0002-frontend-react-vite.md`](../docs/decisions/0002-frontend-react-vite.md).

Pour l'instant, une seule page : un bouton qui appelle `/api/actuator/health` et affiche `UP`, `DOWN` ou « injoignable ».

## Prérequis

- Node 20.19+ (Node 22 recommandé, utilisé en CI et dans le Dockerfile)
- Pour que le bouton réponde en local : l'API Gateway démarrée sur `localhost:8080`

## Commandes

```bash
npm install      # installe les dépendances (npm ci pour une installation reproductible)
npm run dev      # serveur de développement sur http://localhost:5173
npm run build    # vérification TypeScript puis build de production dans dist/
```

## Structure

```
frontend/
├── index.html          # point d'entrée HTML
├── vite.config.ts      # config Vite (plugin React + proxy /api)
├── tsconfig.json       # TypeScript en mode strict
├── package.json
├── nginx.conf          # config nginx de l'image Docker (proxy /api + fallback SPA)
├── Dockerfile          # build Node 22, puis nginx non-root sur le port 8080
└── src/
    ├── main.tsx        # monte l'application React
    ├── App.tsx         # page unique (test de santé)
    └── api/            # couche d'accès à l'API (seul endroit qui connaît /api)
        ├── types.ts      # types TypeScript des contrats du backend
        ├── errors.ts     # ApiError et messages d'erreur en français
        ├── client.ts     # client fetch unique : préfixe /api, timeout, Bearer, erreurs
        └── endpoints.ts  # fonctions d'appel (register, login, listerAssujettis...)
```

## Appeler l'API

Les composants n'écrivent jamais de `fetch` ni de chemin d'API : ils importent les fonctions de `src/api/endpoints.ts`.
Les erreurs arrivent sous forme d'`ApiError` (`kind` : `network`, `timeout`, `unauthorized`, `forbidden`, `server`, `client`) avec un message prêt à afficher en français.
Le jeton JWT n'est pas géré ici : le contexte d'authentification (ticket S42) le fournit avec `setTokenProvider` et se déconnecte via `setUnauthorizedHandler` en cas de 401.

## Fonctionnement du proxy `/api`

Règle du projet : le front n'appelle **jamais** autre chose que `/api/...`. Le navigateur ne parle donc qu'à une seule origine, ce qui évite toute configuration CORS.

- **Développement** : Vite relaie `/api/xxx` vers `http://localhost:8080/xxx` (API Gateway) en retirant le préfixe `/api` (voir `vite.config.ts`).
- **Production (Docker)** : nginx relaie `/api/xxx` vers `http://api-gateway:8080/xxx` (voir `nginx.conf`) et renvoie `index.html` pour toute route inconnue. Le conteneur doit donc être sur le même réseau Docker que le service `api-gateway`.

Exemple : `GET /api/actuator/health` atteint `GET /actuator/health` sur l'API Gateway.
