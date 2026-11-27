# ADR-0005 : Refonte visuelle « portail institutionnel »

- **Statut** : proposé. Remplace l'identité visuelle de l'ADR-0003 (bordeaux, safran, Amiri) ; ses règles d'accessibilité restent valables.
- **Contexte** : l'identité de l'ADR-0003 ne donnait pas assez l'impression d'un service public. La nouvelle direction s'inspire de la mise en page des portails gouvernementaux marocains et a été maquettée avec Lovable avant d'être reportée dans notre code.
- **Décision** : vert profond, liseré rouge, panneaux anthracite, angles presque droits, une seule police (IBM Plex Sans Arabic). En-tête avec menu à rubriques, bandeau de campagne, carrousel d'actualités, rubriques « Actualités », « À la une » et galerie, pied de page sombre, barre de cookies verte. Le logo est remplacé par un sceau propre à la plateforme (`frontend/public/seal.svg`).
- **Limite non négociable** : ni emblème royal, drapeau, armoiries, logo ou nom d'institution, ni photo de personnes réelles. La plateforme collecte des données sensibles et ne doit pas pouvoir passer pour un service officiel. La barre « Projet académique » reste affichée.
- **Images** : illustrations générées par IA, stockées avec le site (`frontend/public/images/`), jamais chargées depuis un service externe.
- **Alternative écartée** : reprendre le code Lovable (Tailwind, shadcn/ui, TanStack). Il aurait fallu refaire le client API, le proxy `/api`, l'image Docker, la CI et les pages de conformité, avec beaucoup plus de dépendances à auditer.
- **Conséquences** : le design reste piloté par `tokens.css` ; les styles du portail sont dans `design-system/portal.css`. La recherche de l'en-tête ne cherche que parmi les pages (aucun moteur côté serveur).
