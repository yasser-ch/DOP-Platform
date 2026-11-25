# ADR 0003 — Journal d'audit immuable (S40 / DODP-50)

Statut : accepté — Date : 2026-11-25

## Contexte
L'administrateur doit pouvoir s'appuyer sur des logs d'audit qu'il est impossible de falsifier sans que cela se voie.

## Décision
1. **Append-only applicatif** : l'entité `AuditEvent` est `@Immutable`, ses colonnes sont `updatable=false`, le dépôt n'expose que `save` et des lectures, et l'API n'a ni PUT, PATCH ni DELETE.
2. **Chaîne de hachages SHA-256** : chaque entrée contient `previous_hash` et `entry_hash = SHA-256(seq, date, acteur, action, ressource, détails, previous_hash)`, avec encodage préfixé par la longueur (pas d'ambiguïté entre champs). La première entrée pointe sur un hash « genesis » de 64 zéros.
3. **Numéros de séquence contigus** (contrainte d'unicité) : une suppression au milieu ou au début est détectée.
4. **Vérification** : `GET /audit/events/verify` (ADMIN) recalcule toute la chaîne et renvoie la première entrée en défaut + le hash de tête.
5. **Acteur imposé par le JWT** : le client ne peut pas choisir l'acteur.
6. **Écriture sérialisée** (verrou + transaction) pour garantir une chaîne linéaire dans une instance.

## Limites connues (travaux futurs)
- Chaîne non signée : quelqu'un qui contrôle la base peut recalculer toute la chaîne. Piste : HMAC avec clé hors base.
- La **troncature de la fin** du journal n'est détectable qu'en comparant le `headHash` à une valeur ancrée ailleurs (export périodique).
- Les droits SQL (`REVOKE UPDATE, DELETE`) relèvent de la migration PostgreSQL (S59) ; H2 en mémoire pour l'instant.
- Une seule instance du service est supposée (verrou local).
