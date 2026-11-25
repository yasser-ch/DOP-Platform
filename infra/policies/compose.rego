# Policy as Code - docker-compose.yml (NF-01/03 exposition, NF-04 secrets, NF-10 sante)
package compose

import rego.v1

# Seuls la gateway (API) et le frontend (interface) publient un port.
exposed_allowed := {"api-gateway", "frontend"}

deny contains msg if {
	some name, svc in input.services
	not name in exposed_allowed
	svc.ports
	msg := sprintf("Le service '%s' publie un port : seuls api-gateway et frontend sont exposes", [name])
}

# Chaque service doit avoir un healthcheck.
deny contains msg if {
	some name, svc in input.services
	not svc.healthcheck
	msg := sprintf("Le service '%s' n'a pas de healthcheck (NF-10)", [name])
}

# Pas de tag latest.
deny contains msg if {
	some name, svc in input.services
	svc.image
	endswith(svc.image, ":latest")
	msg := sprintf("Le service '%s' utilise :latest", [name])
}

# Pas de secret en dur : toute variable sensible doit venir de ${...}.
deny contains msg if {
	some name, svc in input.services
	some key, val in svc.environment
	regex.match(`(?i)(password|secret|token|api_?key)`, key)
	is_string(val)
	not startswith(val, "${")
	msg := sprintf("Secret en clair dans '%s' (variable %s) : utiliser ${VAR} (NF-04)", [name, key])
}
