# ADR-0003 : Brief de design du front-end (design system)

- **Statut** : proposé
- **Décision** : un design system maison, en CSS pur et composants React, dans `frontend/src/design-system/`. Tous les choix visuels (couleurs, typographie, espacements, rayons, ombres) vivent dans un seul fichier de tokens : `tokens.css`.
- **Ton** : institutionnel, sobre et lisible. Fond blanc, bleu nuit comme couleur d'action, peu d'ornements, pas d'animation superflue. Les statuts (succès, alerte, danger) s'accompagnent toujours d'un texte : la couleur ne porte jamais l'information seule.
- **Palette** : texte `#1B2433`, texte atténué `#4A5568`, fond `#FFFFFF`, surface `#F4F6F9`, action `#1F3A5F` (survol `#16304F`), focus `#2B6CB0`. Statuts : succès `#1E6B3A`, alerte `#7A4B00`, danger `#9B1C1C`, info `#1F3A5F`, chacun sur un fond clair associé.
- **Polices** (deux au maximum, auto-hébergées) : Source Serif 4 pour les titres, Inter pour le texte. Elles sont installées par npm (`@fontsource-variable/*`) et embarquées dans le build : aucun CDN externe, aucune requête vers un tiers.
- **Accessibilité** : contrastes mesurés conformes WCAG 2.1 AA (texte ≥ 4,5:1, bordures de champs et focus ≥ 3:1 ; les rapports les plus faibles sont 4,61:1 pour la bordure des champs et 5,42:1 pour le focus). Focus visible sur tous les éléments interactifs, libellés reliés aux champs, modale basée sur `<dialog>` natif.
- **Pourquoi** : pas de bibliothèque de composants (MUI, etc.) pour garder un projet léger, maîtriser l'accessibilité et éviter une dépendance de plus à auditer en CI (SCA). Les composants restent peu nombreux et simples.
- **Alternative écartée** : une bibliothèque de composants toute faite. Plus rapide au départ, mais plus lourde, plus difficile à personnaliser pour une identité institutionnelle, et avec des dépendances transitives nombreuses.
- **Conséquence** : le guide de style est visible sur la page `/ui`. Toute nouvelle page utilise les composants et les tokens plutôt que des couleurs ou des tailles écrites en dur.
