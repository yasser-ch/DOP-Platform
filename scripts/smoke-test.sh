#!/usr/bin/env bash
# Test de fumee de la stack docker compose, via la gateway (seul point d'entree).
# Usage : scripts/smoke-test.sh [http://localhost:8080]
set -uo pipefail
BASE="${1:-http://localhost:8080}"
fail=0
ok()  { echo "OK   - $1"; }
ko()  { echo "FAIL - $1"; fail=1; }
code() { curl -s -o /dev/null -w '%{http_code}' "$@"; }

# 1. La gateway route vers les services (sante via StripPrefix)
c=$(code "$BASE/assujettis/actuator/health"); [ "$c" = 200 ] && ok "gateway -> assujettis-service (health 200)" || ko "gateway -> assujettis health ($c)"

# 2. Inscription + connexion (auth-service)
EMAIL="smoke-$(date +%s)@example.com"; PASS="Smoke-Test-123!"
c=$(code -X POST "$BASE/auth/register" -H 'Content-Type: application/json' -d "{\"email\":\"$EMAIL\",\"password\":\"$PASS\"}")
[ "$c" = 200 ] || [ "$c" = 201 ] && ok "register ($c)" || ko "register ($c)"
TOKEN=$(curl -s -X POST "$BASE/auth/login" -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASS\"}" | python3 -c 'import sys,json; print(json.load(sys.stdin).get("accessToken",""))' 2>/dev/null)
[ -n "$TOKEN" ] && ok "login : JWT recu" || ko "login : pas de JWT"

# 3. Securite : sans jeton 401, avec jeton accepte (declaration-service valide le JWT d'auth-service)
c=$(code "$BASE/declarations"); [ "$c" = 401 ] && ok "GET /declarations sans jeton -> 401" || ko "sans jeton attendu 401, recu $c"
c=$(code "$BASE/declarations" -H "Authorization: Bearer $TOKEN"); [ "$c" = 200 ] && ok "GET /declarations avec jeton -> 200" || ko "avec jeton attendu 200, recu $c"
c=$(code "$BASE/declarations" -H "Authorization: Bearer invalide.jeton.xxx"); [ "$c" = 401 ] && ok "jeton falsifie -> 401" || ko "jeton falsifie attendu 401, recu $c"

# 4. Seule la gateway est exposee : les services internes ne repondent pas depuis l'hote
for port in 8081 8082 8083; do
  c=$(code -m 2 "http://localhost:$port/actuator/health" || true)
  [ "$c" = 000 ] && ok "port $port non expose" || ko "port $port accessible depuis l'hote ($c)"
done

# 5. Rate limiting : une rafale doit produire au moins un HTTP 429
codes=$(curl -s -o /dev/null -w '%{http_code}\n' --parallel --parallel-max 60 "$BASE/assujettis/actuator/health?n=[1-400]" | sort | uniq -c | tr '\n' ' ')
echo "INFO - repartition des codes sur la rafale : $codes"
n429=$(echo "$codes" | grep -o '[0-9]* 429' | cut -d' ' -f1)
n429=${n429:-0}
[ "$n429" -ge 1 ] && ok "rate limiting actif ($n429 reponses 429)" || ko "aucun 429 sur une rafale de 400 requetes"

exit $fail
