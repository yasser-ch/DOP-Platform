package ma.ensa.dop.auth.service;

import ma.ensa.dop.auth.Ressort;
import ma.ensa.dop.auth.Role;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.entity.User;
import ma.ensa.dop.auth.exception.NotMagistratException;
import ma.ensa.dop.auth.exception.SelfRoleChangeException;
import ma.ensa.dop.auth.exception.UserNotFoundException;
import ma.ensa.dop.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAdminService {
    private final UserRepository users;

    public UserAdminService(UserRepository users) { this.users = users; }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse assignRole(Long userId, Role newRole, String currentAdminEmail) {
        User user = users.findById(userId).orElseThrow(UserNotFoundException::new);
        if (user.getEmail().equalsIgnoreCase(currentAdminEmail)) throw new SelfRoleChangeException();
        user.setRole(newRole);
        return UserResponse.from(users.save(user));
    }

    @Transactional
    public UserResponse assignRessort(Long userId, Ressort ressort) {
        User user = users.findById(userId).orElseThrow(UserNotFoundException::new);
        if (user.getRole() != Role.MAGISTRAT) throw new NotMagistratException();
        user.assignRessort(ressort);
        return UserResponse.from(user);
    }
}
