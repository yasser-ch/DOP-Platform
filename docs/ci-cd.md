# Pipeline CI/CD DevSecOps - DOP Platform

Pipeline aligné sur le schéma OWASP DevSecOps : chaque étape est un workflow GitHub Actions, bloquant au-delà d'un seuil défini.

| # | Étape OWASP | Workflow | Outil | Seuil bloquant | Exigence |
|---|---|---|---|---|---|
| 1 | Git | protection de branche, `CODEOWNERS`, PR template | GitHub | PR + checks requis | S2 |
| 2 | Secret scanning | `secrets-scan.yml` | gitleaks 8.21 | tout secret, fichier `.env`/`.pem` versionné | NF-04 |
| 3 | SCA | `security-sca.yml` | OWASP Dependency-Check, Trivy fs, Dependabot | CVSS ≥ 9 | NF-06 |
| 4 | SAST | `sonar.yml` + `ci.yml` | SonarCloud + JaCoCo | Quality Gate rouge | NF-07 |
| 5 | IaC scanning | `iac-scan.yml` | Trivy config, actionlint | HIGH/CRITICAL | NF-06 |
| 6 | Container scanning | `security-sca.yml` (job images) | Trivy image | CRITICAL | NF-06 |
| 7 | DAST | `dast.yml` | OWASP ZAP baseline | règles `FAIL` de `.zap/rules.tsv` | NF-07 |
| 8 | Infra scanning | `dast.yml` | contrôle des ports, utilisateurs, Docker Bench | port non autorisé, conteneur root | NF-03 |
| 9 | Compliance / PaC | `compliance.yml` | OPA Conftest (`infra/policies/*.rego`) | toute règle `deny` | NF-01/03/04/10 |
| - | Test d'intégration | `dast.yml` (`scripts/smoke-test.sh`) | curl | routage, JWT, 401, 429 | S4 |

## Tests locaux

```bash
# Politiques (Conftest)
conftest test --policy infra/policies --namespace dockerfile --parser dockerfile $(git ls-files '*Dockerfile')
conftest test --policy infra/policies --namespace compose docker-compose.yml

# Stack complète + test de fumée
cp .env.example .env && docker compose up -d --build --wait
./scripts/smoke-test.sh http://localhost:8080
```

## Secrets GitHub requis (Settings > Secrets and variables > Actions)

| Secret | Usage |
|---|---|
| `SONAR_TOKEN` | analyse SonarCloud |
| `NVD_API_KEY` | mise à jour de la base NVD (Dependency-Check) |

## Protection de branche (Settings > Branches > `main`, `develop`)

Pull request obligatoire, 1 approbation, branche à jour, et checks requis :
`Build and test (Java 21)`, `Test scoring-service (Python 3.12)`, `SonarCloud analysis`, `gitleaks`,
`OWASP Dependency-Check`, `Trivy`, `Trivy config`, `Lint des workflows`, `Conftest`, `Compose up, smoke test, infra check, ZAP`.

## En cas d'échec

| Job rouge | Cause probable | Action |
|---|---|---|
| gitleaks | secret commité | le révoquer, le retirer de l'historique, le déplacer dans les secrets |
| Dependency-Check / Trivy | CVE critique | monter la dépendance ; sinon suppression datée dans `.github/dependency-check-suppressions.xml` |
| Sonar | Quality Gate | corriger les issues ou la couverture sur le nouveau code |
| Conftest | règle `deny` | corriger le Dockerfile / compose selon le message (NF cité) |
| dast | healthcheck, port, 401/429 | `compose.log` dans l'artefact `dast-infra-reports` |

## Limites connues / suite

- ZAP tourne en baseline (passif) non authentifié ; l'étape suivante est un scan actif authentifié avec l'OpenAPI.
- Pas de publication d'images (GHCR) ni de déploiement : à ajouter sur `main` après validation.
- `frontend` n'est pas encore dans `docker-compose.yml`.
