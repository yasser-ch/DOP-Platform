package ma.ensa.dop.declaration.dto;

import jakarta.validation.constraints.NotNull;
import ma.ensa.dop.declaration.entity.TypeDeclaration;

/** Création d'un brouillon : seul le type est obligatoire. */
public record CreateDeclarationRequest(@NotNull TypeDeclaration type) {}
