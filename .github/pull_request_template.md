## Ticket
DODP-XX

## Description

## Checklist securite
- [ ] Aucun secret dans le code ou l'historique (gitleaks vert)
- [ ] Tests ajoutes/mis a jour, `mvn verify` vert
- [ ] Pas de nouvelle dependance avec CVE critique (SCA vert)
- [ ] Dockerfile : utilisateur non-root, tag d'image explicite
- [ ] Endpoints proteges (JWT + roles) si ajoutes
