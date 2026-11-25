# frontend

Interface web de la plateforme DOP : React 18 + TypeScript (strict) + Vite.
Choix documentés dans [`docs/decisions/`](../docs/decisions/) : framework (0002), design system (0003), conformité des données (0004).

L'interface est pour l'instant un ensemble de **maquettes** (données fictives, formulaires non reliés à l'API), sauf le test de santé de l'accueil qui appelle `/api/actuator/health`. Les vrais écrans suivent les tickets Jira (routage S81, connexion S42, etc.).

## Prérequis

- Node 20.19+ (Node 22 recommandé, utilisé en CI et dans le Dockerfile)
- Pour que le test de santé réponde en local : l'API Gateway démarrée sur `localhost:8080`

## Commandes

```bash
npm install      # installe les dépendances (npm ci pour une installation reproductible)
npm run dev      # serveur de développement sur http://localhost:5173
npm run build    # vérification TypeScript puis build de production dans dist/
```

## Pages (maquettes)

Navigation provisoire par adresse `#/...` (le vrai routage arrive avec le ticket DODP-95). Le guide de style est sur `/ui`.

| Adresse | Page |
|---|---|
| `#/` | Accueil |
| `#/espace-declarant` | Espace déclarant (situation, échéances) |
| `#/assujettis` | Liste des assujettis (recherche, filtre) |
| `#/connexion`, `#/inscription` | Connexion, inscription (consentements et âge) |
| `#/aide`, `#/mentions-legales` | Aide et FAQ, mentions légales |
| `#/confidentialite`, `#/conditions`, `#/cookies` | Politique de confidentialité, conditions d'utilisation, cookies |
| `#/licences` | Licences et crédits (polices, images) |
| `#/desabonnement` | Se désabonner des e-mails |
| `#/suppression-donnees` | Demande de suppression des données |

Les textes juridiques sont des **textes de maquette** à compléter et valider par l'équipe avant toute mise en ligne.

## Structure

```
frontend/
├── index.html            # point d'entrée HTML (icône d'onglet)
├── public/               # fichiers servis tels quels : logo, licences des polices
├── vite.config.ts        # config Vite (plugin React + proxy /api)
├── tsconfig.json         # TypeScript en mode strict
├── nginx.conf            # config nginx de l'image Docker (proxy /api + fallback SPA)
├── Dockerfile            # build Node 22, puis nginx non-root sur le port 8080
└── src/
    ├── main.tsx          # monte l'application (/ui = guide de style)
    ├── App.tsx           # navigation, en-tête, pied de page, bannière de cookies
    ├── consent.tsx       # consentement au stockage non essentiel
    ├── accessibility.ts  # préférences d'affichage (taille du texte, contraste)
    ├── i18n.tsx          # aperçu FR / AR (en-tête, pied de page, accueil)
    ├── api/              # couche d'accès à l'API (seul endroit qui connaît /api)
    ├── design-system/    # tokens CSS, composants, polices, motifs
    └── pages/            # pages (maquettes)
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

## Données personnelles et conformité

- **Pas de donnée inutile** : l'inscription ne demande que l'e-mail et le mot de passe. L'âge (18 ans ou plus) s'atteste par une case à cocher, sans date de naissance.
- **Consentements** : aucune case n'est cochée par défaut. L'inscription exige l'acceptation des conditions et de la politique de confidentialité ; les rappels par e-mail sont facultatifs.
- **Stockage navigateur** : voir la page `#/cookies`. Aucun suivi, aucune mesure d'audience, aucun service tiers (les polices sont hébergées avec le site). La mémorisation des préférences d'affichage n'a lieu qu'avec le consentement de l'utilisateur ; le jeton de connexion reste en mémoire, jamais dans le navigateur.
- **Droits** : pages de désabonnement et de suppression de données.

### À prévoir côté backend (maquettes non reliées)

Ces écrans n'ont pas encore d'endpoint correspondant :

- désabonnement des e-mails (lien présent dans chaque e-mail envoyé par `notification-service`) ;
- demande de suppression des données (avec les règles de conservation légales) ;
- enregistrement du consentement aux e-mails.

## Accessibilité

Lien d'évitement, focus visible, navigation complète au clavier (le focus passe au contenu à chaque changement de page, Échap ferme le menu d'accessibilité), contrastes contrôlés WCAG 2.1 AA (audit automatique des textes de toutes les pages), menu flottant (taille du texte, contraste élevé), icônes décoratives masquées aux lecteurs d'écran.

## Licences

Polices : Amiri et IBM Plex Sans Arabic, sous SIL Open Font License 1.1 (textes dans `public/licenses/`). Voir la page `#/licences` pour le détail, y compris le statut du logo (généré par IA, conditions d'usage de l'outil à vérifier avant publication).
