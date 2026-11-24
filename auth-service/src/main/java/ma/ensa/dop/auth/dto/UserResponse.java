package ma.ensa.dop.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ma.ensa.dop.auth.Ressort;
import ma.ensa.dop.auth.Role;
import ma.ensa.dop.auth.entity.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(Long id, String email, Role role, Ressort ressort) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getRole(), u.getRessort());
    }
}
