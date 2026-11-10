package ma.ensa.dop.auth.dto;

import ma.ensa.dop.auth.Role;

public record UserResponse(Long id, String email, Role role) {}
