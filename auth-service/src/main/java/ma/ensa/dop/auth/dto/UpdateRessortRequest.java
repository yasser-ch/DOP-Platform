package ma.ensa.dop.auth.dto;

import jakarta.validation.constraints.NotNull;
import ma.ensa.dop.auth.Ressort;

public record UpdateRessortRequest(@NotNull Ressort ressort) {}
