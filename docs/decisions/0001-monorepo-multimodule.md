# ADR-0001 : Monorepo Maven multimodule

- **Statut** : proposé
- **Décision** : un seul dépôt Git, un module Maven par microservice Java, `scoring-service` (Python) et `frontend` en dossiers séparés.
- **Pourquoi** : 4-5 services développés en profondeur, petite équipe, une seule pipeline CI à construire (cf. cahier des charges, section 8). Un build `mvn -pl <service> -am` reste possible par service.
- **Alternative écartée** : un dépôt par service (plus lourd : N pipelines, N configs de sécurité, pour un projet académique).
- **Conséquence** : si l'équipe préfère polyrepo, chaque dossier de service est déjà autonome (pom + Dockerfile) et se découpe facilement.
