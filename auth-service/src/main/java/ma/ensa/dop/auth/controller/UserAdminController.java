package ma.ensa.dop.auth.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.auth.dto.UpdateRoleRequest;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.service.UserAdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
    private final UserAdminService service;

    public UserAdminController(UserAdminService service) { this.service = service; }

    @GetMapping
    public List<UserResponse> list() { return service.listUsers(); }

    @PutMapping("/{id}/role")
    public UserResponse assignRole(@PathVariable Long id,
                                   @Valid @RequestBody UpdateRoleRequest req,
                                   Authentication auth) {
        return service.assignRole(id, req.role(), auth.getName());
    }
}
