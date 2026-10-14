# DOP Platform - Déclaration Obligatoire du Patrimoine

> **Démonstrateur académique** inspiré de la loi 54-06. Ce n'est pas un système officiel de la Cour des comptes. Données de test synthétiques uniquement.

Projet DevSecOps - cycle ingénieur, filière Cybersécurité. Architecture microservices Java / Spring Boot (+ un service Python pour le scoring).

## Structure du dépôt

| Dossier | Port | Rôle |
|---|---|---|
| `api-gateway` | 8080 | Point d'entrée unique, routage, rate limiting |
| `auth-service` | 8081 | Authentification, autorisation, gestion des rôles (FR-01 à FR-04) |
| `assujettis-service` | 8082 | Gestion des listes nominatives d'assujettis (FR-05 à FR-08) |
| `declaration-service` | 8083 | Cœur métier : création et soumission des déclarations (FR-09 à FR-16) |
| `document-service` | 8084 | Récépissés PDF et stockage sécurisé de documents (FR-17 à FR-19) |
| `workflow-service` | 8085 | Cycle de contrôle : examen, observation, mise en demeure (FR-20 à FR-26) |
| `reconciliation-service` | 8086 | Recoupement avec administrations externes simulées (FR-27 à FR-30) |
| `notification-service` | 8087 | Notifications et rappels (FR-34 à FR-36) |
| `audit-service` | 8088 | Journalisation immuable et traçabilité (FR-37, FR-38) |
| `reporting-service` | 8089 | Statistiques et tableaux de bord (FR-39, FR-40) |
| `scoring-service` | 8090 | Score de risque par IA (Python / FastAPI, FR-31 à FR-33) |
| `frontend/` | - | Interface web (à définir) |
| `infra/` | - | Docker Compose / Kubernetes, CI/CD, outils de sécurité |
| `docs/` | - | Conception, documentation, ADR |

## Prérequis

- JDK 21
- Maven 3.9+
- Python 3.12+ (uniquement pour `scoring-service`)
- Docker (pour les images)
- Git

## Build

```bash
# Tous les services Java : compilation + tests
mvn clean verify

# Un seul service (et ses dépendances internes)
mvn -pl declaration-service -am clean verify

# Sans les tests
mvn clean package -DskipTests
```

## Lancer un service

```bash
mvn -pl auth-service spring-boot:run
curl http://localhost:8081/actuator/health
```

## scoring-service (Python)

```bash
cd scoring-service
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
pytest
uvicorn app.main:app --port 8090
```

## Images Docker

```bash
mvn -pl auth-service -am package -DskipTests
docker build -t dop/auth-service auth-service
```

## Conventions de travail

- Branches : `main` (stable), `develop`, puis `feature/DOP-<n°ticket Jira>-description`.
- Commits : préfixer par la clé Jira, ex. `DOP-12 ajoute le module auth-service` (le lien avec Jira se fait automatiquement).
- Aucun secret dans le dépôt (NF-04) : `.env` et clés sont ignorés par Git.

## Documents

Voir `docs/` et le cahier des charges du projet.
