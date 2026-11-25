# Gestion des secrets (S52 / NF-04)

**Règle d'or : aucun secret (mot de passe, clé, token, certificat) n'est versionné.**
Les secrets vivent hors du dépôt et sont injectés à l'exécution.

## 1. Où vivent les secrets

| Contexte | Stockage | Injection |
|---|---|---|
| CI (GitHub Actions) | *Settings → Secrets and variables → Actions* | `${{ secrets.NOM }}` dans `env:` d'un step |
| Développement local | Fichier `.env` (ignoré par git), copié depuis `.env.example` | `docker compose` (`env_file`) ou variables d'environnement du shell/IDE |
| Production / staging | Variables d'environnement de la plateforme, ou Vault (voir §6) | Variables d'environnement lues par l'application |

Le dépôt ne contient que des **noms** de variables (`.env.example`, `application.yml`), jamais leurs **valeurs**.

## 2. Inventaire des secrets

> À compléter par l'équipe : une ligne par secret.

| Variable | Usage | Où est-elle définie | Propriétaire |
|---|---|---|---|
| `SONAR_TOKEN` | Analyse SonarCloud | Secret GitHub Actions | |
| `NVD_API_KEY` | OWASP Dependency-Check | Secret GitHub Actions | |
| `DB_PASSWORD` | Mot de passe base de données | `.env` local / env prod | |
| `JWT_SECRET` | Signature des JWT | `.env` local / env prod | |

## 3. Comment les utiliser dans le code

**Spring Boot (`application.yml`)** : référencer une variable, sans valeur par défaut pour les secrets, afin que l'application refuse de démarrer si elle manque.
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
app:
  jwt:
    secret: ${JWT_SECRET}
```

**Docker Compose** : `:?` fait échouer le démarrage si la variable est absente.
```yaml
services:
  db:
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD:?DB_PASSWORD manquant}
  backend:
    env_file: .env
```

**Python (scoring-service)**
```python
import os
DB_PASSWORD = os.environ["DB_PASSWORD"]   # KeyError si absent : voulu
```

**GitHub Actions**
```yaml
- name: Étape qui a besoin d'un secret
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: ./commande
```
Ne jamais faire `echo "$SECRET"`. GitHub masque les secrets enregistrés dans les logs, mais pas leurs dérivés (base64, extraits).

**Frontend** : tout ce qui est embarqué dans le build (`VITE_*`, `REACT_APP_*`, `NEXT_PUBLIC_*`, etc.) est **public**. Aucun secret côté frontend : il passe par le backend.

## 4. Procédures

### Ajouter un nouveau secret
1. Ajouter la variable (nom uniquement, valeur factice) dans `.env.example`.
2. La lire via variable d'environnement dans le code ou la config (§3).
3. Créer la valeur réelle : secret GitHub (CI) et/ou configuration de l'environnement cible.
4. Ajouter la ligne à l'inventaire (§2).
5. Ouvrir la PR : le scan gitleaks doit passer.

### Démarrer en local
```bash
cp .env.example .env     # puis renseigner les vraies valeurs (demander à l'équipe)
docker compose up
```
Le fichier `.env` est dans `.gitignore` et ne doit jamais être commité ni partagé par mail/chat. Utiliser le gestionnaire de mots de passe de l'équipe.

### Rotation
- Rotation tous les **90 jours** pour les tokens et clés, et **immédiatement** en cas de départ d'un membre ayant eu accès.
- Procédure : générer la nouvelle valeur chez le fournisseur → mettre à jour le secret (GitHub / env / Vault) → redéployer → révoquer l'ancienne valeur.

### Fuite d'un secret (incident)
1. **Révoquer et régénérer immédiatement** le secret chez le fournisseur. Supprimer le fichier ou le commit ne suffit pas : l'historique git et les forks conservent la valeur.
2. Mettre à jour le secret partout où il est utilisé.
3. Vérifier les logs d'accès du service concerné sur la période d'exposition.
4. Si la valeur est dans l'historique, la purger (`git filter-repo`) après la révocation, et prévenir l'équipe de re-cloner.
5. Noter l'incident (date, cause, correctif).

## 5. Contrôles automatiques

- **CI** : `.github/workflows/secrets-scan.yml` lance gitleaks sur tout l'historique à chaque push/PR et fait échouer le build en cas de détection. Il bloque aussi les fichiers sensibles versionnés (`.env`, `*.pem`, `*.jks`…).
- **Local (recommandé)** : hook pre-commit qui bloque avant même le commit.
  ```bash
  pip install pre-commit
  pre-commit install
  ```
- **Faux positifs** : les ajouter dans `.gitleaks.toml` (`[allowlist]`) avec une justification en commentaire dans la PR. Ne jamais allowlister un vrai secret : le révoquer.
- **Branch protection** : le job `Gitleaks` doit être un status check obligatoire sur `main`.

## 6. Évolution vers Vault (optionnel)

Aujourd'hui : variables d'environnement. Si l'équipe adopte HashiCorp Vault (ou le gestionnaire de secrets du cloud), seule la source change : l'application continue de lire des variables d'environnement, alimentées par Vault Agent, `spring-cloud-vault` ou l'orchestrateur. Le code et la CI ne changent pas.

