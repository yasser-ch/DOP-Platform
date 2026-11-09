package ma.ensa.dop.auth.dto;

import jakarta.validation.constraints.NotNull;
import ma.ensa.dop.auth.Role;

public record UpdateRoleRequest(@NotNull Role role) {}
