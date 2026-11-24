# Policy as Code - Dockerfile (NF-03 non-root, NF-06 images maitrisees)
package dockerfile

import rego.v1

# Une image finale doit definir un utilisateur non-root.
deny contains msg if {
	not has_user
	msg := "Dockerfile sans instruction USER : l'image finale doit tourner en non-root (NF-03)"
}

has_user if {
	some i
	input[i].Cmd == "user"
	not lower(input[i].Value[0]) == "root"
	not input[i].Value[0] == "0"
}

# Interdit les tags flottants "latest" (ou absents) sur les images de base.
deny contains msg if {
	some i
	input[i].Cmd == "from"
	img := input[i].Value[0]
	not img == "scratch"
	not contains(img, "$")
	not is_build_alias(img)
	not contains(img, ":")
	msg := sprintf("Image de base sans tag explicite : %s (NF-06)", [img])
}

deny contains msg if {
	some i
	input[i].Cmd == "from"
	endswith(input[i].Value[0], ":latest")
	msg := sprintf("Tag 'latest' interdit : %s (NF-06)", [input[i].Value[0]])
}

# Alias d'etape (FROM build AS x puis FROM x) : pas un probleme.
is_build_alias(img) if {
	some i
	input[i].Cmd == "from"
	count(input[i].Value) == 3
	input[i].Value[2] == img
}

# Pas de secret passe en ARG/ENV.
deny contains msg if {
	some i
	input[i].Cmd in {"env", "arg"}
	regex.match(`(?i)(password|secret|token|api_?key)\s*=\s*\S+`, concat(" ", input[i].Value))
	msg := sprintf("Secret potentiel dans %s : %s (NF-04)", [input[i].Cmd, input[i].Value[0]])
}
